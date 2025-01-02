import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.*;

/**
 * Classe représentant l'atelier où les produits sont fabriqués et gérés.
 * L'atelier interagit avec des agents robots pour distribuer les tâches et suivre l'état des produits.
 */
public class atelier extends Agent {

    /**
     * Liste des produits à fabriquer dans l'atelier.
     */
    private List<produit> produits;

    /**
     * Liste des produits terminés par l'atelier.
     */
    private List<produit> endProducts;

    /**
     * Liste des produits non réalisables dans l'atelier.
     */
    private List<produit> clearProducts;

    /**
     * Nombre total de produits à traiter dans l'atelier.
     */
    private int totalProducts;

    /**
     * Dictionnaire contenant les scores des robots pour chaque produit.
     */
    private HashMap<String, HashMap<String, Float>> robotProductScores;

    /**
     * Méthode appelée lors de l'initialisation de l'agent.
     */
    protected void setup() {
        System.out.println("Bonjour ! Agent " + getAID().getName() + " prêt à fonctionner.");

        // Définir les produits et leurs compétences directement dans le code
        HashMap<String, ArrayList<String>> products = new HashMap<>();
        products.put("produit1", new ArrayList<>(Arrays.asList("souder", "peindre")));
        products.put("produit2", new ArrayList<>(Arrays.asList("couper", "peindre")));
        products.put("produit3", new ArrayList<>(Arrays.asList("souder", "couper")));
        products.put("produit4", new ArrayList<>(Arrays.asList("souder", "peindre", "couper")));
        products.put("produit5", new ArrayList<>(Arrays.asList("souder")));
        products.put("produit6", new ArrayList<>(Arrays.asList("peindre")));
        products.put("produit7", new ArrayList<>(Arrays.asList("couper")));

        // Initialisation de la liste des produits à fabriquer
        this.produits = new ArrayList<>();
        for (String productName : products.keySet()) {
            this.produits.add(new produit(productName, products.get(productName)));
        }
        this.totalProducts = this.produits.size();

        // Initialisation des listes auxiliaires et des scores
        this.endProducts = new ArrayList<>();
        this.clearProducts = new ArrayList<>();
        this.robotProductScores = new HashMap<>();

        // Ajout des comportements
        this.addBehaviour(new dispatchProduct(this, 100)); // Envoi des produits aux robots
        this.addBehaviour(new acceptMessage(this)); // Réception des messages des robots
    }

    /**
     * Classe interne pour gérer l'envoi des produits aux robots.
     */
    private class dispatchProduct extends TickerBehaviour {
        private Agent a;

        public dispatchProduct(Agent a, long period) {
            super(a, period);
            this.a = a;
        }

        protected void onTick() {
            if (produits.size() > 0) {
                produit p = produits.get(0); // Produit à fabriquer

                // Création des scores des robots
                HashMap<String, Float> agentsScore = new HashMap<>();
                DFAgentDescription template1 = new DFAgentDescription();
                try {
                    DFAgentDescription[] result = DFService.search(this.a, template1);
                    for (DFAgentDescription agent : result) {
                        agentsScore.put(agent.getName().getLocalName(), 0.0f);
                        for (Iterator it = agent.getAllServices(); it.hasNext(); ) {
                            ServiceDescription service = (ServiceDescription) it.next();
                            if (p.getSkills().containsKey(service.getType()) && !p.getSkills().get(service.getType())) {
                                agentsScore.put(agent.getName().getLocalName(),
                                        agentsScore.get(agent.getName().getLocalName()) + Float.parseFloat(service.getName()));
                            }
                        }
                        if (agentsScore.get(agent.getName().getLocalName()) == 0.0f) {
                            agentsScore.remove(agent.getName().getLocalName());
                        }
                    }
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }

                if (agentsScore.size() == 0) {
                    System.out.println("Aucun robot n'est capable de fabriquer le produit : " + p.getName());
                    clearProducts.add(p);
                    produits.remove(p);
                } else {
                    robotProductScores.put(p.getName(), agentsScore);
                    String assignedAgent = "";
                    float topScore  = 0.0f;
                    for (String agent : agentsScore.keySet()) {
                        if (agentsScore.get(agent) > topScore ) {
                            topScore  = agentsScore.get(agent);
                            assignedAgent = agent;
                        }
                    }
                    agentsScore.remove(assignedAgent);
                    robotProductScores.remove(assignedAgent);
                    produits.remove(p);

                    ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                    message.addReceiver(new AID(assignedAgent, AID.ISLOCALNAME));
                    try {
                        message.setContentObject(p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    this.a.send(message);
                }
            } else {
                if (endProducts.size() + clearProducts.size() == totalProducts) {
                    System.out.println("Tous les produits ont été traités.");
                    System.out.println("Produits finis :");
                    for (produit p : endProducts) {
                        System.out.println(p.getName());
                    }
                    if (clearProducts.size() > 0) {
                        System.out.println("Produits non réalisables :");
                        for (produit p : clearProducts) {
                            System.out.println(p.getName());
                        }
                    }
                    this.stop();
                }
            }
        }
    }

    /**
     * Classe interne pour gérer la réception des messages des robots.
     */
    private class acceptMessage extends CyclicBehaviour {
        private Agent a;

        public acceptMessage(Agent a) {
            this.a = a;
        }

        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REFUSE) {
                    produit p;
                    try {
                        p = (produit) msg.getContentObject();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Agent " + msg.getSender().getLocalName() + " refuse de fabriquer le produit " + p.getName());
                    produits.add(p);
                } else if (msg.getPerformative() == ACLMessage.INFORM) {
                    produit produit;
                    try {
                        produit = (produit) msg.getContentObject();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    if (produit.isDone()) {
                        System.out.println("Le produit " + produit.getName() + " est terminé.");
                        endProducts.add(produit);
                    } else {
                        System.out.println("Le produit " + produit.getName() + " n'est pas terminé.");
                        produits.add(produit);
                    }
                }
            }
        }
    }

    /**
     * Méthode appelée lors de la terminaison de l'agent.
     */
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " terminating.");
    }
}

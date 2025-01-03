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
 * Classe représentant l'atelier où les véhicules sont réparés et gérés.
 * L'atelier interagit avec des agents mécaniciens pour distribuer les tâches et suivre l'état des véhicules.
 */
public class atelier extends Agent {

    /**
     * Liste des véhicules à réparer dans l'atelier.
     */
    private List<produit> produits;

    /**
     * Liste des véhicules réparés par l'atelier.
     */
    private List<produit> endProducts;

    /**
     * Liste des véhicules non réparables dans l'atelier.
     */
    private List<produit> clearProducts;

    /**
     * Nombre total de véhicules à traiter dans l'atelier.
     */
    private int totalProducts;

    /**
     * Dictionnaire contenant les scores des mécaniciens pour chaque véhicule.
     */
    private HashMap<String, HashMap<String, Float>> robotProductScores;

    /**
     * Méthode appelée lors de l'initialisation de l'agent.
     */
    protected void setup() {
        System.out.println("Initialisation : L'agent " + getAID().getName() + " est prêt à fonctionner!");

        // Définir les véhicules et leurs compétences directement dans le code
        HashMap<String, ArrayList<String>> products = new HashMap<>();
        products.put("Voiture1", new ArrayList<>(Arrays.asList("diagnostiquer", "peindre")));
        products.put("Voiture2", new ArrayList<>(Arrays.asList("souder", "diagnostiquer")));
        products.put("Voiture3", new ArrayList<>(Arrays.asList("peindre", "diagnostiquer")));
        products.put("Voiture4", new ArrayList<>(Arrays.asList("assembler", "souder")));
        products.put("Voiture5", new ArrayList<>(Arrays.asList("diagnostiquer")));
        products.put("Voiture6", new ArrayList<>(Arrays.asList("peindre", "souder")));
        products.put("Voiture7", new ArrayList<>(Arrays.asList("diagnostiquer", "assembler")));


        // Initialisation de la liste des véhicules à réparer
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
        this.addBehaviour(new dispatchProduct(this, 100)); // Envoi des véhicules aux mécaniciens
        this.addBehaviour(new acceptMessage(this)); // Réception des messages des mécaniciens
    }

    /**
     * Classe interne pour gérer l'envoi des véhicules aux mécaniciens.
     */
    private class dispatchProduct extends TickerBehaviour {
        private Agent a;

        public dispatchProduct(Agent a, long period) {
            super(a, period);
            this.a = a;
        }

        protected void onTick() {
            if (produits.size() > 0) {
                produit p = produits.get(0); // Véhicule à réparer

                // Création des scores des mécaniciens
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
                    System.out.println("Aucun mécanicien compétent n'est disponible pour traiter le véhicule : " + p.getName());
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
                    System.out.println("Traitement terminé : " + endProducts.size() + " véhicules réparés, " + clearProducts.size() + " non réparables.");

                    System.out.println("Véhicules réparés :");
                    for (produit p : endProducts) {
                        System.out.println(p.getName());
                    }
                    if (clearProducts.size() > 0) {
                        System.out.println("Véhicules non réparables :");
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
     * Classe interne pour gérer la réception des messages des mécaniciens.
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
                    System.out.println("L'agent " + msg.getSender().getLocalName() + " a refusé de réparer le véhicule : " + p.getName());
                    produits.add(p);
                } else if (msg.getPerformative() == ACLMessage.INFORM) {
                    produit produit;
                    try {
                        produit = (produit) msg.getContentObject();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    if (produit.isDone()) {
                        System.out.println("Le véhicule " + produit.getName() + " a été réparé avec succès !");
                        endProducts.add(produit);
                    } else {
                        System.out.println("Le véhicule " + produit.getName() + " n'est pas terminé.");
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

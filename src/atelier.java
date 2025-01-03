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
import java.util.concurrent.ThreadLocalRandom;

/**
 * Classe représentant l'atelier oo les véhicules gérés
 * l'atelier interagit avec des agents mécaniciens pour distribuer les taches et suivre l'état des véhicules
 */
public class atelier extends Agent {

    /**
     * liste des véhicules à réparer dans l'atelier
     */
    private List<produit> produits;

    /**
     * liste des véhicules réparés par l'atelier
     */
    private List<produit> endProducts;

    /**
     * liste des véhicules non réparables dans l'atelier
     */
    private List<produit> clearProducts;

    /**
     * nombre total de véhicules à traiter dans l'atelier
     */
    private int totalProducts;

    /**
     * les scores des mécaniciens pour chaque véhicule
     */
    private HashMap<String, HashMap<String, Float>> robotProductScores;

    /**
     * mthode appelée lors de l'initialisation de l'agent
     */
    protected void setup() {
        System.out.println("Initialisation : L'agent " + getAID().getName() + " est prêt a fonctionner!");

        // définition des véhicules et leurs compétences(j'ai fait plusieurs tests avec le nombre de voitures et competences differents)
        
        //TEST 1
        HashMap<String, ArrayList<String>> products = new HashMap<>();
        products.put("Voiture1", new ArrayList<>(Arrays.asList("diagnostiquer", "peindre")));
        products.put("Voiture2", new ArrayList<>(Arrays.asList("souder", "diagnostiquer")));
        products.put("Voiture3", new ArrayList<>(Arrays.asList("peindre", "diagnostiquer")));
        products.put("Voiture4", new ArrayList<>(Arrays.asList("assembler", "souder")));
        products.put("Voiture5", new ArrayList<>(Arrays.asList("diagnostiquer")));
        products.put("Voiture6", new ArrayList<>(Arrays.asList("peindre", "souder")));
        products.put("Voiture7", new ArrayList<>(Arrays.asList("diagnostiquer", "assembler")));

        //TEST 2
        // HashMap<String, ArrayList<String>> products = new HashMap<>();
        // products.put("Voiture1", new ArrayList<>(Arrays.asList("diagnostiquer", "peindre")));
        // products.put("Voiture2", new ArrayList<>(Arrays.asList("souder", "diagnostiquer")));
        // products.put("Voiture3", new ArrayList<>(Arrays.asList("peindre", "assembler")));
        // products.put("Voiture4", new ArrayList<>(Arrays.asList("assembler", "souder")));
        // products.put("Voiture5", new ArrayList<>(Arrays.asList("diagnostiquer", "verifier")));
        // products.put("Voiture6", new ArrayList<>(Arrays.asList("peindre", "souder", "polir")));
        // products.put("Voiture7", new ArrayList<>(Arrays.asList("diagnostiquer", "assembler", "verifier")));
        // products.put("Voiture8", new ArrayList<>(Arrays.asList("assembler", "polir")));
        // products.put("Voiture9", new ArrayList<>(Arrays.asList("diagnostiquer", "peindre", "verifier")));
        // products.put("Voiture10", new ArrayList<>(Arrays.asList("souder", "assembler", "polir")));
        // products.put("Voiture11", new ArrayList<>(Arrays.asList("peindre", "diagnostiquer", "assembler")));
        // products.put("Voiture12", new ArrayList<>(Arrays.asList("assembler", "peindre", "souder", "verifier")));
        // products.put("Voiture13", new ArrayList<>(Arrays.asList("polir", "verifier")));
        // products.put("Voiture14", new ArrayList<>(Arrays.asList("diagnostiquer", "assembler", "souder", "peindre")));
        // products.put("Voiture15", new ArrayList<>(Arrays.asList("peindre", "polir", "verifier")));


        // initialisation de la liste des véhicules à réparer
        this.produits = new ArrayList<>();
        for (String productName : products.keySet()) {
            this.produits.add(new produit(productName, products.get(productName)));
        }
        this.totalProducts = this.produits.size();

        this.endProducts = new ArrayList<>();
        this.clearProducts = new ArrayList<>();
        this.robotProductScores = new HashMap<>();

        this.addBehaviour(new dispatchProduct(this, 100)); // envoi des véhicules aux mecaniciens
        this.addBehaviour(new acceptMessage(this)); // reception des messages des mécaniciens
    }

    /**
     * Classe i pour gérer l'envoi des véhicules aux mécaniciens
     */
    private class dispatchProduct extends TickerBehaviour {
        private Agent a;

        public dispatchProduct(Agent a, long period) {
            super(a, period);
            this.a = a;
        }

        protected void onTick() {
            if (produits.size() > 0) {
                produit p = produits.get(0); // véhicule a reparer

                // scores des mécaniciens
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
                    System.out.println("Aucun mécanicien competent pour : " + p.getName());
                    clearProducts.add(p);
                    produits.remove(p);
                } else {
                    robotProductScores.put(p.getName(), agentsScore);
                    String assignedAgent = "";
                    float topScore = 0.0f;
                    for (String agent : agentsScore.keySet()) {
                        if (agentsScore.get(agent) > topScore) {
                            topScore = agentsScore.get(agent);
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
                    System.out.println(p.getName() + " assigné au mécanicien " + assignedAgent);
                }
            } else {
                if (endProducts.size() + clearProducts.size() == totalProducts) {
                    System.out.println("Traitement terminé : " + endProducts.size() + " véhicules réparés, " + clearProducts.size() + " non réparables!");

                    System.out.println("Vehicules réparés :");
                    for (produit p : endProducts) {
                        System.out.println(p.getName());
                    }
                    if (clearProducts.size() > 0) {
                        System.out.println("Vehicules non reparables :");
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
     * gérer la réception des messages des mécaniciens
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
                    System.out.println("L'agent " + msg.getSender().getLocalName() + " ne peut pas réparer le véhicule : " + p.getName());

                    // probabilité pour réessayer ou déléguer
                    double prob = ThreadLocalRandom.current().nextDouble();
                    if (prob < 0.5) {
                        System.out.println("L'agent " + msg.getSender().getLocalName() + " va réessayer de réparer le véhicule : " + p.getName());
                        produits.add(p);
                    } else {
                        System.out.println("L'agent " + msg.getSender().getLocalName() + " délegue la réparation du véhicule : " + p.getName());
                        produits.add(p); // Remets dans la liste pour les autres agents
                    }
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
                        System.out.println("Le véhicule " + produit.getName() + " n'est pas terminé!!");
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
        System.out.println("Agent " + getAID().getName() + " terminé");
    }
}

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Classe représentant un agent mécanicien.
 * Les mécaniciens possèdent des compétences et réparent les véhicules envoyés par l'atelier.
 */
public class robot extends Agent {
    /** Compétences du mécanicien avec leur niveau associé. */
    private HashMap<String, Float> competences;

    /** Liste des véhicules en attente de réparation. */
    private List<produit> produits;

    /** Temps nécessaire pour exécuter une compétence. */
    private double time;

    /**
     * Retourne l'ensemble des compétences du mécanicien.
     * @return Un dictionnaire contenant les compétences et leur niveau associé.
     */
    public HashMap<String, Float> getCompetences() {
        return competences;
    }

    /**
     * Définit les compétences du mécanicien.
     * @param competences Un dictionnaire contenant les compétences et leur niveau associé.
     */
    public void setCompetences(HashMap<String, Float> competences) {
        this.competences = competences;
    }

    /**
     * Retourne la liste des véhicules en attente de réparation.
     * @return Une liste de véhicules.
     */
    public List<produit> getProduits() {
        return produits;
    }

    /**
     * Définit la liste des véhicules à réparer.
     * @param produits Une liste de véhicules.
     */
    public void setProduits(List<produit> produits) {
        this.produits = produits;
    }

    /**
     * Méthode d'initialisation de l'agent mécanicien.
     */
    protected void setup() {
        System.out.println("Bienvenue ! L'agent " + getAID().getName() + " est prêt.");
    
        // Temps nécessaire pour chaque compétence
        this.time = 500.0;
    
        // Définir les compétences disponibles directement dans le code
        List<String> allCompetences = List.of("souder", "peindre", "diagnostiquer");
        this.produits = new ArrayList<>();
    
        // Attribution aléatoire des compétences au robot
        this.competences = new HashMap<>();
        boolean hasCompetence = false;
    
        for (String comp : allCompetences) {
            if (Math.random() > 0.5) { // 50% de chance d'avoir chaque compétence
                this.competences.put(comp, (float) Math.random());
                hasCompetence = true;
            }
        }
    
        // Si aucune compétence n'a été attribuée, attribuer une compétence par défaut
        if (!hasCompetence) {
            String defaultCompetence = allCompetences.get((int) (Math.random() * allCompetences.size()));
            this.competences.put(defaultCompetence, (float) Math.random());
            System.out.println("Le mécanicien " + this.getAID().getLocalName() + " n'avait pas de compétence, il reçoit : " + defaultCompetence);
        }
    
        System.out.println("Compétences du mécanicien " + this.getAID().getLocalName() + " : " + this.competences);
    
        // Enregistrement des compétences dans le DF (Directory Facilitator)
        DFAgentDescription template = new DFAgentDescription();
        for (String comp : competences.keySet()) {
            ServiceDescription sd = new ServiceDescription();
            sd.setType(comp);
            sd.setName(this.competences.get(comp).toString());
            template.addServices(sd);
        }
        try {
            DFService.register(this, template);
        } catch (FIPAException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement des compétences : " + e.getMessage());
        }
    
        // Ajout des comportements de l'agent
        this.addBehaviour(new acceptMessage());
        this.addBehaviour(new applySkills(this, 1000));
    }
    

    /**
     * Comportement pour appliquer des compétences sur les véhicules.
     */
    private class applySkills extends TickerBehaviour {
        public applySkills(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if (produits.size() > 0) {
                produit p = produits.get(0);
                for (String comp : p.getSkills().keySet()) {
                    if (p.getSkills().get(comp)) {
                        continue;
                    }
                    if (competences.containsKey(comp)) {
                        System.out.println("L'agent " + getAID().getName() + " répare la compétence " + comp + " pour le véhicule " + p.getName());
                        try {
                            Thread.sleep((long) (time * (1 - competences.get(comp))));
                        } catch (InterruptedException e) {
                            throw new RuntimeException("Erreur pendant la réparation : " + e.getMessage());
                        }
                        p.finishSkill(comp);
                    }
                }
                if (p.isDone()) {
                    System.out.println("L'agent " + getAID().getName() + " a terminé la réparation du véhicule " + p.getName());
                } else {
                    System.out.println("L'agent " + getAID().getName() + " a partiellement réparé le véhicule " + p.getName());
                }

                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.addReceiver(new AID("eva", AID.ISLOCALNAME));
                try {
                    message.setContentObject(p);
                } catch (IOException e) {
                    throw new RuntimeException("Erreur lors de l'envoi du véhicule : " + e.getMessage());
                }
                send(message);
                produits.remove(0);
                System.out.println("L'agent " + getAID().getName() + " a envoyé le véhicule " + p.getName() + " à l'atelier.");
            }
        }
    }

    /**
     * Comportement pour recevoir des messages d'autres agents.
     */
    private class acceptMessage extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                System.out.println("L'agent " + getAID().getName() + " a reçu un message de " + msg.getSender().getName());
                if (produits.size() >= 3) {
                    System.out.println("L'agent " + getAID().getName() + " a atteint la limite de véhicules qu'il peut traiter.");
                    produit p;
                    try {
                        p = (produit) msg.getContentObject();
                    } catch (Exception e) {
                        throw new RuntimeException("Erreur lors de la lecture du message : " + e.getMessage());
                    }
                    ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
                    reply.addReceiver(new AID("eva", AID.ISLOCALNAME));
                    try {
                        reply.setContentObject(p);
                    } catch (IOException e) {
                        throw new RuntimeException("Erreur lors de la création de la réponse : " + e.getMessage());
                    }
                    send(reply);
                } else {
                    try {
                        produit p = (produit) msg.getContentObject();
                        produits.add(p);
                        ACLMessage reply = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        reply.addReceiver(new AID("eva", AID.ISLOCALNAME));
                        reply.setContentObject(p);
                        send(reply);
                    } catch (Exception e) {
                        throw new RuntimeException("Erreur lors du traitement du message : " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Actions à exécuter avant la terminaison de l'agent.
     */
    protected void takeDown() {
        System.out.println("L'agent " + getAID().getName() + " termine son exécution.");
    }
}


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
import java.util.concurrent.ThreadLocalRandom;

/**
 * Classe représentant un agent mécanicien
 * les mecaniciens posedent des compétences et réparent les véhicules envoyés par l'atelier
 */
public class robot extends Agent {
    /** compétences du mécanicien avec leur niveau  */
    private HashMap<String, Float> competences;

    /** une liste des vehicules enattente de réparation */
    private List<produit> produits;

    /** Temps nécessaire pour exécuter une competence */
    private double time;

    /** la probabilité de terminer une compétence dans une tentative defini a 0.7 */
    private double probabiliteReussite = 0.7;

    /**
     * Retourne l'ensemble des compétences du mécanicien
     * @return un dictionnaire contenant les compétences et leur niveau associee
     */
    public HashMap<String, Float> getCompetences() {
        return competences;
    }

    /**
     * définit les compétences du mecanicien.
     * @param competences 1 dictionnaire contenant les compétences et leur niveau 
     */
    public void setCompetences(HashMap<String, Float> competences) {
        this.competences = competences;
    }

    /**
     * retourne la liste des vrhicules en attente de reparation.
     * @return 1 liste de véhicules.
     */
    public List<produit> getProduits() {
        return produits;
    }

    /**
     * Définit la liste des véhicules à réparer
     * @param produits 1liste de véhicules
     */
    public void setProduits(List<produit> produits) {
        this.produits = produits;
    }

    /**
     * méthode d'initialisation de l'agentmécanicien
     */
    protected void setup() {
        System.out.println("L'agent " + getAID().getName() + " est prêt!");

        // le emps necessaire pour chaque compétence
        this.time = 500.0;

        // définition des compétences 

        //TEST 1
        List<String> allCompetences = List.of("souder", "peindre", "diagnostiquer");
        
        //TEST 2
        //List<String> allCompetences = List.of("souder", "peindre", "diagnostiquer", "assembler", "polir", "verifier");

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

        // Si aucune compétence n'a été attribuée on attribue1 competence par defaut
        if (!hasCompetence) {
            String defaultCompetence = allCompetences.get((int) (Math.random() * allCompetences.size()));
            this.competences.put(defaultCompetence, (float) Math.random());
            System.out.println("Le mécanicien " + this.getAID().getLocalName() + " n'avait pas de compétence, il va recevoir : " + defaultCompetence);
        }

        System.out.println("Compétences du mécanicien " + this.getAID().getLocalName() + " : " + this.competences);

        // enregistrement des compétences dans DF
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
            throw new RuntimeException("!!!probleme lors de l'enregistrement des compétences: " + e.getMessage());
        }

        this.addBehaviour(new acceptMessage());
        this.addBehaviour(new applySkills(this, 1000));
    }

    /**
     * appliquer des competences sur les véhicules
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
                        System.out.println("L'agent " + getAID().getName() + " répare " + comp + " pour " + p.getName());
                        try {
                            Thread.sleep((long) (time * (1 - competences.get(comp))));
                        } catch (InterruptedException e) {
                            throw new RuntimeException("!!erreur pendant la réparation : " + e.getMessage());
                        }

                        // probabilité dereussite
                        if (ThreadLocalRandom.current().nextDouble() <= probabiliteReussite) {
                            p.finishSkill(comp);
                            System.out.println("Agent " + getAID().getName() + " a réussi à réparer " + comp + " pour le véhicule " + p.getName());
                        } else {
                            System.out.println("Agent " + getAID().getName() + " n'a pas réussi à réparer " + comp + " pour le véhicule " + p.getName());
                            break;
                        }
                    }
                }
                if (p.isDone()) {
                    System.out.println("Agent " + getAID().getName() + " a terminé la réparation du véhicule " + p.getName());
                } else {
                    System.out.println("Agent " + getAID().getName() + " a partiellement réparé le véhicule " + p.getName());
                }

                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.addReceiver(new AID("eva", AID.ISLOCALNAME));
                try {
                    message.setContentObject(p);
                } catch (IOException e) {
                    throw new RuntimeException("!!!probleme lors de l'envoi du véhicule : " + e.getMessage());
                }
                send(message);
                produits.remove(0);
                System.out.println("L'agent " + getAID().getName() + " a envoyé le véhicule " + p.getName() + " à l'atelier.");
            }
        }
    }

    /**
     * recevoir des messages d'autres agents.
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
                        throw new RuntimeException("!!erreur lors de la lecture du message : " + e.getMessage());
                    }
                    ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
                    reply.addReceiver(new AID("eva", AID.ISLOCALNAME));
                    try {
                        reply.setContentObject(p);
                    } catch (IOException e) {
                        throw new RuntimeException("!!erreur réponse : " + e.getMessage());
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
                        throw new RuntimeException("!!erreur traitement du message : " + e.getMessage());
                    }
                }
            }
        }
    }

    protected void takeDown() {
        System.out.println("L'agent " + getAID().getName() + " terminé");
    }
}



import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Classe représentant un vehicule à réparer dans un garage 
 * chaque véhicule a un nomet une liste de compétences nécessaires à sa réparation
 */
public class produit implements Serializable {

    /**
     * nom du véhicule.
     */
    private String name;

    /**
     * si le véhicule est libre
     */
    private boolean free;

    /**
     * si le véhicule est entièrement réparee
     */
    private boolean done;

    /**
     * liste descompétences nécessaires a la reparation du vehicule avec un statut pour chaque compétence
     * si la compétence a été réalisé -> true
     */
    private HashMap<String, Boolean> skills;

    /**
     * probabilité de réussite pour terminer une réparation 
     */
    private double probabiliteReussite;

    /**
     * temps estimé pour terminer une réparation 
     */
    private long tempsTraitement;

    /**
     * nombre de tentatives effectuées pour ce produit.
     */
    private int tentatives;

    /**
     * Constructeur pour initialiser un véhicule avec un nom et une liste de compétences
     * @param name nom du véhicule
     * @param skills liste des compétences nécessaires pour réparer le véhicule
     */
    public produit(String name, List<String> skills) {
        this.name = name;
        this.skills = new HashMap<>();
        for (String skill : skills) {
            this.skills.put(skill, false); 
        }
        this.free = true;
        this.done = false;
        this.probabiliteReussite = 0.7; // la probabilité de réussite 0.7 (on peut le changer)
        this.tempsTraitement = 3000;   // temps de traitement  (3 secondes)
        this.tentatives = 0;
    }

    /**
     * @return true si le véhicule est libre, false sinon
     */
    public boolean isFree() {
        return free;
    }

    /**
     * Mets a jour le statut du véhicule
     * @param free Nouveau statut de libre
     */
    public void setFree(boolean free) {
        this.free = free;
    }

    /**
     * verifie si toutes les compétences nécessaires ont été réalisées
     * Si oui le véhicule est considéré comme entièrement réparé
     * @return true si le véhicule est réparé false sinon
     */
    public boolean isDone() {
        for (String skill : skills.keySet()) {
            if (!skills.get(skill)) {
                return false;
            }
        }
        this.done = true;
        return done;
    }

    /**
     * mets à jour le statut de réparation du véhicule
     * Si le véhicule est marqué comme réparé, toutes les compétences sont marquées comme réalisees
     * @param done un nouuveau statut de réparé
     */
    public void setDone(boolean done) {
        this.done = done;
        if (done) {
            for (String skill : skills.keySet()) {
                skills.put(skill, true);
            }
        }
    }

    /**
     * retourne la liste des compétences nécessaires au véhicule
     * @return HashMap contenant les compétences et leur statut
     */
    public HashMap<String, Boolean> getSkills() {
        return skills;
    }

    /**
     * mets à jour la liste des compétences du véhicule
     * @param skills nouvelle liste des compétences avec statuts
     */
    public void setSkills(HashMap<String, Boolean> skills) {
        this.skills = skills;
    }

    /**
     * @return Nom du véhicule.
     */
    public String getName() {
        return name;
    }

    /**
     * mets à jour le nom du véhicule
     * @param name Nouveau nom du véhicule.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * ecrit une compétence comme réalisée pour ce véhicule
     * @param skill la compétence à marquer comme réalisée
     */
    public void finishSkill(String skill) {
        this.skills.put(skill, true);
    }

    /**
     * @return probabilité de réussite.
     */
    public double getProbabiliteReussite() {
        return probabiliteReussite;
    }

    /**
     * @param probabiliteReussite nouvelle probabilité de réussite.
     */
    public void setProbabiliteReussite(double probabiliteReussite) {
        this.probabiliteReussite = probabiliteReussite;
    }

    /**
     * @return temps de traitement 
     */
    public long getTempsTraitement() {
        return tempsTraitement;
    }

    /**
     * @param tempsTraitement nouveau temps de traitement.
     */
    public void setTempsTraitement(long tempsTraitement) {
        this.tempsTraitement = tempsTraitement;
    }

    /**
     * @return nombre de tentatives.
     */
    public int getTentatives() {
        return tentatives;
    }

    /**
     * incrémente le nombre de tentatives pour le produit
     */
    public void incrementerTentatives() {
        this.tentatives++;
    }
}

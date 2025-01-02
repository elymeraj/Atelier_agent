/**
 * Classe représentant un produit dans le système.
 * Chaque produit possède un nom, un état (en cours ou terminé),
 * ainsi qu'une liste des compétences nécessaires à sa fabrication.
 */
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class produit implements Serializable {

    /**
     * Nom du produit.
     */
    private String name;

    /**
     * Indique si le produit est libre (non encore pris en charge).
     */
    private boolean free;

    /**
     * Indique si le produit est terminé.
     */
    private boolean done;

    /**
     * Liste des compétences nécessaires à la fabrication du produit, avec un statut pour chaque compétence.
     * true signifie que la compétence a été réalisée.
     */
    private HashMap<String, Boolean> skills;

    /**
     * Constructeur pour initialiser un produit avec un nom et une liste de compétences.
     * @param name Le nom du produit.
     * @param skills La liste des compétences nécessaires pour fabriquer le produit.
     */
    public produit(String name, List<String> skills) {
        this.name = name;
        this.skills = new HashMap<>();
        for (String skill : skills) {
            this.skills.put(skill, false); // Initialisation des compétences à non réalisées.
        }
        this.free = true;
        this.done = false;
    }

    /**
     * Retourne si le produit est libre (non pris en charge).
     * @return true si le produit est libre, false sinon.
     */
    public boolean isFree() {
        return free;
    }

    /**
     * Met à jour le statut de libre du produit.
     * @param free Nouveau statut de libre.
     */
    public void setFree(boolean free) {
        this.free = free;
    }

    /**
     * Vérifie si toutes les compétences nécessaires ont été réalisées.
     * Si c'est le cas, le produit est considéré comme terminé.
     * @return true si le produit est terminé, false sinon.
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
     * Met à jour le statut de terminé du produit.
     * Si le produit est marqué comme terminé, toutes les compétences sont marquées comme réalisées.
     * @param done Nouveau statut de terminé.
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
     * Retourne la liste des compétences nécessaires au produit.
     * @return HashMap contenant les compétences et leur statut.
     */
    public HashMap<String, Boolean> getSkills() {
        return skills;
    }

    /**
     * Met à jour la liste des compétences du produit.
     * @param skills Nouvelle liste des compétences avec leurs statuts.
     */
    public void setSkills(HashMap<String, Boolean> skills) {
        this.skills = skills;
    }

    /**
     * Retourne le nom du produit.
     * @return Nom du produit.
     */
    public String getName() {
        return name;
    }

    /**
     * Met à jour le nom du produit.
     * @param name Nouveau nom du produit.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Marque une compétence comme réalisée pour ce produit.
     * @param skill La compétence à marquer comme réalisée.
     */
    public void finishSkill(String skill) {
        this.skills.put(skill, true);
    }
}

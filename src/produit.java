import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Classe représentant un véhicule à réparer dans un garage mécanique.
 * Chaque véhicule a un nom et une liste de compétences nécessaires à sa réparation.
 */
public class produit implements Serializable {

    /**
     * Nom du véhicule.
     */
    private String name;

    /**
     * Indique si le véhicule est libre (non encore pris en charge).
     */
    private boolean free;

    /**
     * Indique si le véhicule est entièrement réparé.
     */
    private boolean done;

    /**
     * Liste des compétences nécessaires à la réparation du véhicule, avec un statut pour chaque compétence.
     * true signifie que la compétence a été réalisée.
     */
    private HashMap<String, Boolean> skills;

    /**
     * Constructeur pour initialiser un véhicule avec un nom et une liste de compétences.
     * @param name Le nom du véhicule.
     * @param skills La liste des compétences nécessaires pour réparer le véhicule.
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
     * Retourne si le véhicule est libre (non pris en charge).
     * @return true si le véhicule est libre, false sinon.
     */
    public boolean isFree() {
        return free;
    }

    /**
     * Met à jour le statut de libre du véhicule.
     * @param free Nouveau statut de libre.
     */
    public void setFree(boolean free) {
        this.free = free;
    }

    /**
     * Vérifie si toutes les compétences nécessaires ont été réalisées.
     * Si c'est le cas, le véhicule est considéré comme entièrement réparé.
     * @return true si le véhicule est réparé, false sinon.
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
     * Met à jour le statut de réparation du véhicule.
     * Si le véhicule est marqué comme réparé, toutes les compétences sont marquées comme réalisées.
     * @param done Nouveau statut de réparé.
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
     * Retourne la liste des compétences nécessaires au véhicule.
     * @return HashMap contenant les compétences et leur statut.
     */
    public HashMap<String, Boolean> getSkills() {
        return skills;
    }

    /**
     * Met à jour la liste des compétences du véhicule.
     * @param skills Nouvelle liste des compétences avec leurs statuts.
     */
    public void setSkills(HashMap<String, Boolean> skills) {
        this.skills = skills;
    }

    /**
     * Retourne le nom du véhicule.
     * @return Nom du véhicule.
     */
    public String getName() {
        return name;
    }

    /**
     * Met à jour le nom du véhicule.
     * @param name Nouveau nom du véhicule.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Marque une compétence comme réalisée pour ce véhicule.
     * @param skill La compétence à marquer comme réalisée.
     */
    public void finishSkill(String skill) {
        this.skills.put(skill, true);
    }
}

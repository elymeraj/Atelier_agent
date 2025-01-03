import java.util.Scanner;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

/**
 * Classe principale pour démarrer le système
 */
public class Main {

    /**
     * Point d'entrée principal de l'application multi-agents.
     * Cette méthode initialise la plateforme JADE, l'atelier et les agents robots.
     * 
     * @param args Les arguments de ligne de commande .
     */
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            /**
             * Message d'accueil et demande du nombre de mécaniciens
             */
            System.out.println("===================================================");
            System.out.println(" Bienvenue dans le simulateur d'atelier mécanique !");
            System.out.println("===================================================");
            System.out.print("Combien de mécaniciens souhaitez-vous ajouter ? : ");
            int nbRobots = scanner.nextInt();
            scanner.nextLine(); 

            /**
             * Tableau pour les noms des des mécaniciens.
             */
            String[] robotNames = new String[nbRobots];

            // Demande des noms pour chaque mécanicien
            for (int i = 0; i < nbRobots; i++) {
                System.out.print("Veuillez entrer le nom du  mécanicien " + (i + 1) + " : ");
                robotNames[i] = scanner.nextLine();
            }

            /**
             * Initialisation de la plateforme JADE avec une interface graphique activée.
             */
            System.out.println("\n--- Lancement de la plateforme JADE ---");
            Runtime runtime = Runtime.instance();
            ProfileImpl profile = new ProfileImpl();
            profile.setParameter("gui", "true");
            ContainerController mainContainer = runtime.createMainContainer(profile);

            // Création et démarrage de l'agent Atelier
            System.out.println("Démarrage de l'atelier principal...");
            AgentController atelier = mainContainer.createNewAgent("eva", "atelier", null);
            atelier.start();

            /**
             * Création et démarrage des agents robots en fonction des noms spécifiés.
             */
            for (String robotName : robotNames) {
                System.out.println("Activation du mécanicien : " + robotName);
                AgentController robot = mainContainer.createNewAgent(robotName, "robot", null);
                robot.start();
            }

            System.out.println("\n============================================");
            System.out.println(" Tous les agents ont été lancés avec succès !");
            System.out.println("===============================================\n");
        } catch (Exception e) {
            /**
             * Gestion des erreurs lors de l'exécution du programme.
             */
            System.err.println("Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }
}





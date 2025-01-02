// import java.util.Scanner;
// import jade.core.Runtime;
// import jade.core.ProfileImpl;
// import jade.wrapper.AgentController;
// import jade.wrapper.ContainerController;

// public class Main {
//     public static void main(String[] args) {
//         try {
//             Scanner scanner = new Scanner(System.in);

//             // Demander le nombre de robots
//             System.out.print("Entrez le nombre de robots : ");
//             int nbRobots = scanner.nextInt();
//             scanner.nextLine(); // Consommer la ligne restante

//             String[] robotNames = new String[nbRobots];

//             // Demander les noms des robots
//             for (int i = 0; i < nbRobots; i++) {
//                 System.out.print("Entrez le nom du robot " + (i + 1) + " : ");
//                 robotNames[i] = scanner.nextLine();
//             }

//             // Initialisation de la plateforme JADE
//             Runtime runtime = Runtime.instance();
//             ProfileImpl profile = new ProfileImpl();
//             profile.setParameter("gui", "true");
//             ContainerController mainContainer = runtime.createMainContainer(profile);

//             // Lancer l'agent atelier
//             AgentController atelier = mainContainer.createNewAgent("eva", "atelier", null);
//             atelier.start();

//             // Lancer les agents robots
//             for (String robotName : robotNames) {
//                 AgentController robot = mainContainer.createNewAgent(robotName, "robot", null);
//                 robot.start();
//             }

//             System.out.println("Tous les agents sont démarrés !");
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }


import java.util.Scanner;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

/**
 * Classe principale pour démarrer le système multi-agents.
 */
public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            /**
             * Message d'accueil et demande du nombre de robots.
             */
            System.out.println("===============================");
            System.out.println(" Bienvenue dans votre simulateur multi-agents !");
            System.out.println("===============================");
            System.out.print("Combien de robots souhaitez-vous créer ? : ");
            int nbRobots = scanner.nextInt();
            scanner.nextLine(); // Consommer la ligne restante

            /**
             * Tableau pour les noms des robots.
             */
            String[] robotNames = new String[nbRobots];

            // Demande des noms pour chaque robot
            for (int i = 0; i < nbRobots; i++) {
                System.out.print("Veuillez entrer le nom du robot " + (i + 1) + " : ");
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
            System.out.println("Lancement de l'agent de gestion principale (Atelier)...");
            AgentController atelier = mainContainer.createNewAgent("eva", "atelier", null);
            atelier.start();

            /**
             * Création et démarrage des agents robots en fonction des noms spécifiés.
             */
            for (String robotName : robotNames) {
                System.out.println("Activation du robot : " + robotName);
                AgentController robot = mainContainer.createNewAgent(robotName, "robot", null);
                robot.start();
            }

            System.out.println("\n===============================");
            System.out.println(" Tous les agents ont été lancés avec succès !");
            System.out.println("===============================\n");
        } catch (Exception e) {
            /**
             * Gestion des erreurs lors de l'exécution du programme.
             */
            System.err.println("Oups ! Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

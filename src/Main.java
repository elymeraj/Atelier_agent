import java.util.Scanner;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

/**
 * classe principale pour démarrer le système
 */
public class Main {

    /**
     * initialisation de la plateforme jade
     * 
     * @param args les arguments
     */
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("===================================================");
            System.out.println(" Bienvenue dans le simulateur d'atelier mécanique !");
            System.out.println("===================================================");
            System.out.print("Combien de mécaniciens souhaitez-vous ajouter ? : ");
            int nbRobots = scanner.nextInt();
            scanner.nextLine(); 

            /**
             * les noms des des mécaniciens
             */
            String[] robotNames = new String[nbRobots];

            // Demande des noms pour chaque mécanicien
            for (int i = 0; i < nbRobots; i++) {
                System.out.print("Entrez le nom du  mécanicien " + (i + 1) + " : ");
                robotNames[i] = scanner.nextLine();
            }

            System.out.println("\n=====Lancement de JADE=====");
            Runtime runtime = Runtime.instance();
            ProfileImpl profile = new ProfileImpl();
            profile.setParameter("gui", "true");
            ContainerController mainContainer = runtime.createMainContainer(profile);

            System.out.println("démarrage de l'atelier principal: ");
            AgentController atelier = mainContainer.createNewAgent("eva", "atelier", null);
            atelier.start();

            /**
             * création et démarrage des agents robots en fonction des noms 
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
            
            System.err.println("erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}





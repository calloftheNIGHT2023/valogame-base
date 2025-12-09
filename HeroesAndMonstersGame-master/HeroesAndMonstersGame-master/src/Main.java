import controller.GameController;
import java.util.Scanner;
import legends.config.LegendsConfig;
import legends.controller.LegendsOfValorController;

public class Main {
    public static void main(String[] args) {
        Scanner menuScanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nSelect a game mode:");
            System.out.println(" 1) Heroes and Monsters (Classic)");
            System.out.println(" 2) Legends of Valor (Prototype)");
            System.out.println(" Q) Quit");
            System.out.print("> ");
            String choice = menuScanner.nextLine().trim().toUpperCase();
            if (choice.isEmpty()) {
                continue;
            }
            switch (choice) {
                case "1":
                    new GameController().start();
                    break;
                case "2":
                    new LegendsOfValorController(menuScanner, LegendsConfig.DEFAULT_RANDOM_SEED).start();
                    break;
                case "Q":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }
}
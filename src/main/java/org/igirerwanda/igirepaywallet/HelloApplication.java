package org.igirerwanda.igirepaywallet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.igirerwanda.igirepaywallet.lab1.Lab1Console;
import org.igirerwanda.igirepaywallet.lab2.Lab2Console;

import java.io.IOException;
import java.util.Scanner;


public class HelloApplication extends Application {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--gui")) {
            // Launch GUI mode
            launch(args);
        } else {
            // Run console menu mode
            runConsoleMenu();
        }
    }

    /**
     * Console-based menu system for running demos
     */
    private static void runConsoleMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║     IgirePay Wallet System             ║");
            System.out.println("║     Backend Phase 1 Capstone           ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("\nMain Menu:");
            System.out.println("  1. Run Lab 1: Interactive Testing");
            System.out.println("  2. Run Lab 2: Database Integration");
            System.out.println("  3. Run Lab 3: Console Application (Coming Soon)");
            System.out.println("  4. Launch GUI Application");
            System.out.println("  0. Exit");
            System.out.print("\nSelect an option (0-4): ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (choice) {
                    case 1:
                        Lab1Console.run();
                        break;
                    case 2:
                        Lab2Console.run();
                        break;
                    case 3:
                        System.out.println("\n⚠️  Lab 3 is coming soon...");
                        break;
                    case 4:
                        System.out.println("\nLaunching GUI mode...");
                        launch();
                        return;
                    case 0:
                        System.out.println("\nThank you for using IgirePay. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("\n✗ Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("\n✗ Invalid input. Please enter a number between 0 and 4.");
                scanner.nextLine();  // Clear invalid input
            }
        }

        scanner.close();
    }

    /**
     * JavaFX start method - called when GUI mode is launched
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("IgirePay Wallet System");
        stage.setScene(scene);
        stage.show();
    }
}

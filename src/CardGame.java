import java.util.LinkedList;
import java.util.Queue;

import java.util.Scanner;

import java.io.FileNotFoundException;


public class CardGame {
    volatile Queue<Card> pack; // Change to read pack
    volatile Player[] players;
    volatile Queue<Card>[] decks;

    volatile int winner = -1;

    public static void main(String[] args) {
        CardGame game = new CardGame();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter the number of players:");
        String players = scanner.nextLine();

        System.out.println("Please enter location of pack to load:");
        String packPath = scanner.nextLine();

        scanner.close();

        try {
            game.pack = Utils.readPack(packPath);
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
        
        // Ask for number players
        // players = num from asking
        // decks = num from asking

        // Distribute round-robin to player's hands
        // Distribute round-robin to decks
        // Pack is now empty

        // Check if someone's won
        // Don't start gameloop if yes
        // Start gameloop if no

        // Game loop
            // Call threads
            // Players do their turn (draw, discard, check win)
        // If winning flag, stop threads
        // Print winner and write files
    }
}

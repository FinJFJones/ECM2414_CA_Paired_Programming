import java.io.FileNotFoundException;
import java.util.Queue;
import java.util.Scanner;

public class CardGame {
    private volatile static Queue<Card> pack; // Change to read pack
    volatile Player[] players;
    volatile Queue<Card>[] decks;

    volatile int winner = -1;

    public static void main(String[] args) {
        int numPlayers = getNumPlayers();
        pack = getPack(numPlayers);
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

    public static int getNumPlayers() {
        int numPlayers = -1;
        Scanner scanner = new Scanner(System.in);
        while (numPlayers < 1) {

            System.out.println("Please enter the number of players:");
            String playersString = scanner.nextLine();
            try {
                numPlayers = Integer.parseInt(playersString);

            } catch (NumberFormatException e) {
                System.out.println("Invalid number of players.");
            }
        }
        scanner.close();
        return numPlayers;
    }

    public static Queue<Card> getPack(int n) {
        Scanner scanner = new Scanner(System.in);

        while (pack == null) {
            System.out.println("Please enter location of pack to load:");
            String packPath = scanner.nextLine();

            try {
                pack = Utils.readPack(packPath);
            } catch (FileNotFoundException e) {
                System.err.println(e);
            }

            if (pack.size() != 8*n){
                pack = null;
            }
        }

        scanner.close();
        return pack;
    }
}

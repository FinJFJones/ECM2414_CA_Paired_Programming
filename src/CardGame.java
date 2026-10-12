import java.io.FileNotFoundException;
import java.util.Queue;
import java.util.Scanner;


public class CardGame {
    private volatile static Queue<Card> pack; // Change to read pack
    private volatile static Player[] players;
    private volatile static  Queue<Card>[] decks;

    volatile int winner = -1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numPlayers = getNumPlayers(scanner);
        pack = getPack(scanner,numPlayers);
        scanner.close();

        players = generatePlayers(numPlayers);

        Dealing.dealHands(pack, players);


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

    public static int getNumPlayers(Scanner scanner) {
        int numPlayers = -1;
        while (numPlayers < 1) {

            System.out.println("Please enter the number of players:");
            String playersString = scanner.nextLine();
            try {
                numPlayers = Integer.parseInt(playersString);

            } catch (NumberFormatException e) {
                System.out.println("Invalid number of players.");
            }
        }

        return numPlayers;
    }

    public static Queue<Card> getPack(Scanner scanner, int n) {
        while (pack == null) {
            System.out.println("Please enter location of pack to load:");
            String packPath = scanner.nextLine();

            try {
                pack = Dealing.readPack(packPath);
                if (pack.size() != 8 * n) {
                pack = null;
                System.out.println("Invalid pack size.");
                }
            } catch (FileNotFoundException | NumberFormatException e) {
                System.err.println(e);
            }   

            
            }
        
        return pack;
    }

    public static Player[] generatePlayers(int n){
        Player[] p = new Player[n];
        for (int i = 0; i < n; i++) {
            p[i] = new Player(i+1);
        }
        return p;
    }

}

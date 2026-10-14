import java.io.FileNotFoundException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class CardGame {
    private volatile static Queue<Card> pack; // Change to read pack
    private static Player[] players;
    private static ConcurrentLinkedQueue<Card>[] decks;

    public volatile static AtomicInteger winner;

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        int numPlayers = getNumPlayers(scanner);
        pack = getPack(scanner,numPlayers);
        scanner.close();

        decks = new ConcurrentLinkedQueue[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            decks[i] = new ConcurrentLinkedQueue<>();
        }
        
        players = generatePlayers(numPlayers);
        

        Dealing.dealHands(pack, players);
        Dealing.dealDecks(pack, decks);

        winner = checkPlayersWon();

        if (winner.get() == -1) {
            for (Player player: players) {
                player.start();
            }
        }

        while (winner.get() == -1) {
            
        }

        for (Player player: players) {
            player.join();
        }
        
        System.out.println(winner);
        
        // Ask for number players                                   | Done
        // players = num from asking                                | Done
        // decks = num from asking                                  | Done

        // Distribute round-robin to player's hands                 | Done
        // Distribute round-robin to decks                          | Done
        // Pack is now empty                                        | Done

        // Check if someone's won                                   | Done
        // Don't start gameloop if yes                              | Done
        // Start gameloop if no                                     | Done

        // Game loop                                                | Done
        // Call threads                                             | Done
        // Players do their turn (draw, discard, check win)         | Done
        // If winning flag, stop threads                            | Done
        // Print winner and write files                             | Not done
    }

    private static int getNumPlayers(Scanner scanner) {
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

    private static Queue<Card> getPack(Scanner scanner, int n) {
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

    private static Player[] generatePlayers(int n){
        Player[] p = new Player[n];
        for (int i = 0; i < n; i++) {
            p[i] = new Player(i+1, decks[i], decks[(i+1)%n]);
        }
        return p;
    }

    private static AtomicInteger checkPlayersWon() {
        for (int i = 0; i < players.length; i++) {
            if (players[i].hasWon()) {
                System.out.println(i+1);
                return new AtomicInteger(i+1);
            }
        }
        return new AtomicInteger(-1);
    }

}

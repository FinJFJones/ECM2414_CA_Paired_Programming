package cardplayingsimulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class CardGame {
    private static Queue<Card> pack;
    private static Player[] players;
    private static List<ConcurrentLinkedQueue<Card>> decks;

    final static AtomicInteger winner = new AtomicInteger(-1);

    public static void main(String[] args) throws InterruptedException {
        File outputsDir = new File("outputs");
        outputsDir.mkdir();
        OutputHandler.wipeDir("outputs");

        Scanner scanner = new Scanner(System.in);
        int numPlayers = getNumPlayers(scanner);
        pack = getPack(scanner, numPlayers);
        scanner.close();

        decks = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            decks.add( new ConcurrentLinkedQueue<>() );
        }

        players = generatePlayers(numPlayers);

        Dealing.dealHands(pack, players);

        for (Player player : players) {
            String text = "player " + player.getPlayerId() + " initial hand" + player.showHand();
            OutputHandler.writeToTextFile(player.getFilePath(), text, true);
        }

        Dealing.dealDecks(pack, decks);
        
        winner.set(checkPlayersWon());

        for (Player player : players) {
            player.start();
        }

        while (winner.get() == -1) {
        }

        for (Player player : players) {
            player.join();
        }

        for (int i = 0; i < decks.size(); i++) {
            String currentDeckPath = "outputs/deck" + (i + 1) + "_output.txt";
            OutputHandler.createTextFile(currentDeckPath);
            OutputHandler.writeToTextFile(currentDeckPath,
                    "deck" + (i + 1) + " contents:" + Dealing.showDeck(decks.get(i)),
                    false);
        }
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
        pack = null;
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

    private static Player[] generatePlayers(int n) {
        Player[] p = new Player[n];
        for (int i = 0; i < n; i++) {
            p[i] = new Player(i + 1, decks.get(i), decks.get((i + 1) % n), n);
        }
        return p;
    }

    public static int checkPlayersWon() {
        for (int i = 0; i < players.length; i++) {
            if (players[i].hasWon()) {
                return i+1;
            }
        }
        return -1;
    }
}

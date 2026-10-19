package cardplayingsimulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides the main static method to play the simulation.
 * 
 * @author Thomas Russell and Finley Jones.
 */

public class CardGame {
    private static Queue<Card> pack;
    private static Player[] players;
    private static List<ConcurrentLinkedQueue<Card>> decks;

    final static AtomicInteger winner = new AtomicInteger(-1);

    /**
     * The main static method to play the simulation.
     * 
     * @param args String, CLI input.
     * @throws InterruptedException if threads are interrupted before they finish
     *                              whilst waiting for join().
     */
    public static void main(String[] args) throws InterruptedException {
        String cwd = System.getProperty("user.dir");
        File outputsDir = new File(cwd + "/outputs");
        outputsDir.mkdir();
        OutputHandler.wipeDir(cwd + "/outputs");

        Scanner scanner = new Scanner(System.in);
        int numPlayers = getNumPlayers(scanner);
        pack = getPack(scanner, numPlayers);
        scanner.close();

        decks = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            decks.add(new ConcurrentLinkedQueue<>());
        }

        players = generatePlayers(numPlayers);

        Dealing.dealHands(pack, players);

        for (Player player : players) {
            String text = "player " + player.getPlayerId() + " initial hand" + player.showHand();
            OutputHandler.writeToTextFile(player.getFilePath(), text, true);
        }

        Dealing.dealDecks(pack, decks);

        winner.set(checkPlayersWon());

        try {
            for (Player player : players) {
                player.start();
            }

            for (Player player : players) {
                player.join();
            }
        } finally {
            stopAllPlayers();
        }

        for (int i = 0; i < decks.size(); i++) {
            String currentDeckPath = cwd + "/outputs/deck" + (i + 1) + "_output.txt";
            OutputHandler.createTextFile(currentDeckPath);
            OutputHandler.writeToTextFile(currentDeckPath,
                    "deck" + (i + 1) + " contents:" + Dealing.showDeck(decks.get(i)),
                    false);
        }
    }

    /**
     * Gets and validates the number of players to play the game from CLI input.
     * 
     * @param scanner Scanner, the scanner to use for getting CLI input.
     * @return int, the number of players.
     */
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

    /**
     * Gets and validates the input pack.
     * 
     * @param scanner Scanner, the scanner to use for getting CLI input.
     * @param n       int, the number of players.
     * @return Queue<Card>, the pack of cards.
     */
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

    /**
     * Generates the player instances with IDs 1 -> n.
     * 
     * @param n int, the number of players.
     * @return Player[], the array of player instances.
     */
    private static Player[] generatePlayers(int n) {
        Player[] p = new Player[n];
        for (int i = 0; i < n; i++) {
            p[i] = new Player(i + 1, decks.get(i), decks.get((i + 1) % n), n);
        }
        return p;
    }

    /**
     * For each player check if they have won, used at game start.
     * 
     * @return int, the ID of the winning player, -1 if no winner.
     */
    public static int checkPlayersWon() {
        for (int i = 0; i < players.length; i++) {
            if (players[i].hasWon()) {
                return i + 1;
            }
        }
        return -1;
    }

    public static void stopAllPlayers() throws InterruptedException {
        Player[] currentPlayers = players;

        if (currentPlayers == null) {
            return;
        }

        for (Player player : currentPlayers) {
            player.stopThread();
        }

        for (Player player : currentPlayers) {
            if (player != Thread.currentThread()) {
                player.join();
            }
        }
    }
}

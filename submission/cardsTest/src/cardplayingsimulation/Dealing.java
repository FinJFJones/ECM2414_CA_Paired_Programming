package cardplayingsimulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Provides static methods for dealing Cards to hands and decks and for reading a pack of cards in.
 * @author Thomas Russell and Finley Jones.
 */

class Dealing {
    /**
     * Reads a pack in from a given location and converts the found values into a queue of Card objects.
     * @param path String, the path to the pack.
     * @return Queue<Card>, the pack.
     * @throws FileNotFoundException throws if file location is invalid.
     * @throws NumberFormatException throws if a card in the pack is not valid.
     */
    static Queue<Card> readPack(String path) throws FileNotFoundException, NumberFormatException {
        Queue<Card> pack = new LinkedList<>();

        try (Scanner scanner = new Scanner(new File(path))) {
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();

                try {
                    int cardValue = Integer.parseInt(nextLine);
                    Card card = new Card(cardValue);
                    pack.add(card);
                } catch (NumberFormatException e) {
                    throw e;
                }
            }
        }
        return pack;
    }

    /**
     * Round-robin style deals hands to all players.
     * @param pack Queue<Card>, the pack of cards.
     * @param players Player[], the array of players.
     */
    static void dealHands(Queue<Card> pack, Player[] players) {
        for (int i = 0; i < 4; i++) {
            for (Player player : players) {
                Card card = pack.poll();
                player.deal(card,i);
            }
        }
    };
    /**
     * Round-robin style deals remaining cards to decks.
     * @param pack Queue<Card>, the pack of cards.
     * @param decks List<ConcurrentLinkedQueue<Card>>, the list of decks.
     */
    static void dealDecks(Queue<Card> pack, List<ConcurrentLinkedQueue<Card>> decks) {
        for (int i = 0; i < 4; i++) {
            for (ConcurrentLinkedQueue<Card> deck : decks) {
                Card card = pack.poll();
                deck.add(card);
            }
        }
    }
    /**
     * Converts a deck to string format for output file.
     * @param deck ConcurrentLinkedQueue<Card>, the deck to convert.
     * @return String, the deck in String format as required.
     */
    static String showDeck(ConcurrentLinkedQueue<Card> deck) {
        Card[] cards = deck.toArray(new Card[0]);
        String deckString = "";
        for (Card card : cards){
            deckString += " "+card.getValue();
        }
        return deckString;
    }
}

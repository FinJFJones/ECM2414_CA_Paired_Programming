import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Dealing {
    public static Queue<Card> readPack(String path) throws FileNotFoundException, NumberFormatException {
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


    public static void dealHands(Queue<Card> pack, Player[] players) {
        for (int i = 0; i < 4; i++) {
            for (Player player : players) {
                Card card = pack.poll();
                player.deal(card);
            }
        }
    };

    public static void dealDecks(Queue<Card> pack, ConcurrentLinkedQueue<Card>[] decks) {
        for (int i = 0; i < 4; i++) {
            for (ConcurrentLinkedQueue<Card> deck : decks) {
                Card card = pack.poll();
                deck.add(card);
            }
        }
    }
}

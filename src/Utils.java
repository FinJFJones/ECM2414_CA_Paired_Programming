import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Utils {
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
}

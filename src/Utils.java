import java.util.LinkedList;
import java.util.Queue;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Utils {
    public static Queue<Card> readPack(String path) throws FileNotFoundException {
        Queue<Card> pack = new LinkedList<>();

        Scanner scanner = new Scanner(new File(path));

        while (scanner.hasNextInt()) {
            int cardValue = scanner.nextInt();
            Card card = new Card(cardValue);
            pack.add(card);
        }

        scanner.close();
        
        return pack;
    }
}

package test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

import cardplayingsimulation.CardGame;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;
import java.util.regex.*;
import java.util.*;

import java.io.File;

final class Card {
    final private int value;

    /**
     * Constructor for card.
     * 
     * @param value int, the face value of the card.
     */
    Card(int value) {
        this.value = value;
    }

    /**
     * Gets the face value of the card.
     * 
     * @return int, the value.
     */
    int getValue() {
        return this.value;
    }
}

public class miscTests {
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("playerTestProvider")
    public void cardConservationBatch(String argument) throws InterruptedException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(argument.getBytes());
        String packPath = argument.split("\n")[1].trim();
        int numPlayers = Integer.parseInt(argument.split("\n")[0].trim());
        runWithInputConservation(in, packPath, numPlayers);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("playerTestProvider")
    public void discardBatch(String argument) throws InterruptedException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(argument.getBytes());
        int numPlayers = Integer.parseInt(argument.split("\n")[0].trim());
        runWithInputDiscardCheck(in, numPlayers);
    }

    static Arguments[] playerTestProvider() {
        int tests = 100;
        int[] playerAmounts = { 2, 4, 10, 50 };
        Arguments[] strings = new Arguments[tests * playerAmounts.length];

        String currentString;
        for (int i = 0; i < playerAmounts.length; i++) {
            for (int j = 1; j <= tests; j++) {
                currentString = playerAmounts[i] + "\nsrc/testPacks/normalTests/" + playerAmounts[i] + "Player/"
                        + playerAmounts[i] + "_player_test_" + j + ".txt";
                strings[(i * tests) + j - 1] = Arguments.of(currentString);
            }
        }

        return strings;
    }

    @Test
    public void fifoDeck() {
        ConcurrentLinkedQueue<Card> deck = new ConcurrentLinkedQueue<>();
        Card first = new Card(1);
        Card second = new Card(2);
        Card third = new Card(3);

        deck.add(first);
        deck.add(second);
        deck.add(third);

        assertSame(first, deck.poll());
        assertSame(second, deck.poll());
        assertSame(third, deck.poll());
    }

    void runWithInputDiscardCheck(ByteArrayInputStream in, int num) throws InterruptedException, IOException {
        System.setIn(in);

        CardGame.main(new String[0]);

        String cwd = System.getProperty("user.dir");
        for (int i = 1; i <= num; i++) {
            String player_txt = "player" + (i) + "_output.txt";
            List<String> lines = Files.readAllLines(Paths.get(cwd + "/outputs/" + player_txt));

            for (String line : lines) {
                assertFalse(line.equals("player " + (i) + " discards a " + (i) + " to deck " + ((i + 1) % num)));
            }
        }
    }

    void runWithInputConservation(ByteArrayInputStream in, String packPath, int num) throws InterruptedException, IOException {
        List<Integer> values = new ArrayList<>();

        System.setIn(in);

        Queue<Card> pack = readPack(packPath);

        CardGame.main(new String[0]);

        String cwd = System.getProperty("user.dir");
        for (int i = 1; i <= num; i++) {
            String deck_txt = "deck" + (i) + "_output.txt";
            String player_txt = "player" + (i) + "_output.txt";

            String deckLine = Files.readAllLines(Paths.get(cwd + "/outputs/" + deck_txt)).get(0);

            List<String> lines = Files.readAllLines(Paths.get(cwd + "/outputs/" + player_txt));
            String playerLine = lines.get(lines.size() - 1);
            
            values.addAll(extractInts(deckLine));
            values.addAll(extractInts(playerLine));

        }

        assertEquals(pack.size(),values.size());

        List<Integer> packList = new ArrayList<>();
        for (Card card : pack) {
            packList.add(card.getValue());
        }

        Collections.sort(packList);
        Collections.sort(values);
        assertEquals(packList,values);

    }

    
    static List<Integer> extractInts(String line) {
        final Pattern NUMBERS = Pattern.compile("\\d+");

        List<Integer> result = new ArrayList<>();
        Matcher m = NUMBERS.matcher(line);
        while (m.find()) {
            result.add(Integer.parseInt(m.group()));
        }
        return result.subList(1,result.size());
}
}

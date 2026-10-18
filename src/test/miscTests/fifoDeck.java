package test.miscTests;

import java.io.ByteArrayInputStream;
import java.util.Queue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class Card {
    final private int value;
    /**
     * Constructor for card.
     * @param value int, the face value of the card.
     */
    Card(int value) {
        this.value = value;
    }
    /**
     * Gets the face value of the card.
     * @return int, the value.
     */
    int getValue(){
        return this.value;
    }
}

public class fifoDeck {
    @Test 
    void fifoDeck(){
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

   
}
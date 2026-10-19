package cardplayingsimulation;

/**
 * Immutable, thread-safe Card class.
 * @author Thomas Russell and Finley Jones.
 */

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

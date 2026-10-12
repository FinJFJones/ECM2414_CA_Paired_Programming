import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Player {
    PriorityQueue<Card> hand;

    public Player(int Id) {
        this.hand = new PriorityQueue<>(
            (a, b) -> Boolean.compare(a.value == Id, b.value == Id)
        );
    }


    public void deal(Card card) {
        this.hand.add(card);
    }

    public boolean hasWon() {
        Card firstCard = this.hand.peek();
        for (Card card: this.hand) {
            if (card.value != firstCard.value) {
                return false;
            }
        }
        return true;
    }
}

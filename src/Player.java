import java.util.PriorityQueue;

public class Player {
    PriorityQueue<Card> hand;

    public Player(int Id) {
        this.hand = new PriorityQueue<>(
            (a, b) -> Boolean.compare(a.value == Id, b.value == Id)
        );
    }


    public void deal(Card card){
        this.hand.add(card);
    }
}

import java.util.PriorityQueue;

public class Player {
    final int Id;
    PriorityQueue<Card> hand;

    public Player(int Id) {
        this.Id = Id;
        this.hand = new PriorityQueue<>(
            (a, b) -> Boolean.compare(a.value == Id, b.value == Id)
        );
    }
}

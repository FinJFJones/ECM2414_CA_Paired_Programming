import java.util.PriorityQueue;
import java.util.Queue;

public class Player extends Thread{
    private volatile Queue<Card> drawDeck;
    private volatile Queue<Card> discardDeck;

    private volatile boolean done = false;
    private int Id;
    private PriorityQueue<Card> hand;

    public Player(int Id, Queue<Card> drawDeck, Queue<Card> discardDeck) {
        this.Id = Id;
        this.hand = new PriorityQueue<>(
            (a, b) -> Boolean.compare(a.value == Id, b.value == Id)
        );

        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;
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

    @Override
    public void run() {
        while (!done) {
            if (this.drawDeck.size() > 0) {
                Card cardDrawn = this.drawDeck.poll();
                this.hand.add(cardDrawn);

                Card cardDiscarded = this.hand.poll();
                this.discardDeck.add(cardDiscarded);
            }
            if (this.hasWon()) {
                CardGame.winner = this.Id;
            }
        }
    }

    public void stopThread() {
        done = true;
    }
}

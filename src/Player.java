import java.util.PriorityQueue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class Player extends Thread{
    private volatile ConcurrentLinkedQueue<Card> drawDeck;
    private volatile ConcurrentLinkedQueue<Card> discardDeck;

    private volatile boolean done = false;
    final private int Id;
    final private PriorityQueue<Card> hand;

    public Player(int Id, ConcurrentLinkedQueue<Card> drawDeck, ConcurrentLinkedQueue<Card> discardDeck) {
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
            if (!this.drawDeck.isEmpty()) {
                Card cardDrawn = this.drawDeck.poll();
                this.hand.add(cardDrawn);

                Card cardDiscarded = this.hand.poll();
                this.discardDeck.add(cardDiscarded);
            }
            if (CardGame.winner.get() != -1) {
                this.stopThread();
            }
            else if (this.hasWon()) {
                CardGame.winner.compareAndSet(-1, this.Id);
            }
        }
    }

    public void stopThread() {
        done = true;
    }
}

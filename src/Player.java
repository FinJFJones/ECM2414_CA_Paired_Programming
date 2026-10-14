import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Player extends Thread{
    private volatile ConcurrentLinkedQueue<Card> drawDeck;
    private volatile ConcurrentLinkedQueue<Card> discardDeck;

    private volatile boolean done = false;
    final private int Id;
    final private PriorityQueue<Card> hand;
    final private String filePath;

    public Player(int Id, ConcurrentLinkedQueue<Card> drawDeck, ConcurrentLinkedQueue<Card> discardDeck) {
        this.Id = Id;
        this.hand = new PriorityQueue<>(
            (card1, card2) -> Boolean.compare(card1.getValue() == Id, card2.getValue() == Id)
        );

        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;

        this.filePath = "outputs/player" +this.Id + "_output.txt";
        OutputHandler.createTextFile(filePath);
    }

    public void deal(Card card) {
        this.hand.add(card);
    }

    public boolean hasWon() {
        Card firstCard = this.hand.peek();
        for (Card card: this.hand) {
            if (card.getValue() != firstCard.getValue()) {
                return false;
            }
        }
        return true;
    }

    public String getFilePath(){
        return this.filePath;
    }

    public int getPlayerId(){
        return this.Id;
    }

    public String showHand(){
        Card[] cards = this.hand.toArray(new Card[0]);
        String handString = "";
        for (Card card : cards){
            handString += " "+card.getValue();
        }
        return handString;
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
                System.out.println("Player "+ this.Id +" wins");
            }
        }
    }

    public void stopThread() {
        done = true;
    }
}

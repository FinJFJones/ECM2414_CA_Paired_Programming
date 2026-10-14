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

    private void outputTurn(Card cardDrawn, Card cardDiscarded){
        int playerId = this.getPlayerId();

        String drawnText = "player " + playerId + " draws a " + cardDrawn.getValue() + " from deck " + playerId;
        OutputHandler.writeToTextFile(this.getFilePath(),drawnText);

        String discardText = "player " + playerId + " discard a " + cardDiscarded.getValue() + " from deck " + (playerId+1)%4;
        OutputHandler.writeToTextFile(this.getFilePath(),discardText);

        String handText = "player " + playerId + " current hand is" + this.showHand();
        OutputHandler.writeToTextFile(this.getFilePath(),handText);
    }

    private void outputLossGame(){
        int playerId = this.getPlayerId();
        int winnerId = CardGame.winner.get();

        String line1 = "player " + winnerId + " has informed player " + playerId + " that player " + winnerId + " has won";
        String line2 = "player " + playerId + " exits";
        String line3 = "player " + playerId + " hand:" + this.showHand();

        String text = line1 + "\n" + line2 + "\n" + line3;
        OutputHandler.writeToTextFile(this.getFilePath(), text);
    }

    private void outputWinGame(){
        int playerId = this.getPlayerId();

        String line1 = "player " + playerId + " wins";
        String line2 = "player " + playerId + " exits";
        String line3 = "player " + playerId + " final hand:" + this.showHand();

        String text = line1 + "\n" + line2 + "\n" + line3;
        OutputHandler.writeToTextFile(this.getFilePath(), text);
    }

    @Override
    public void run() {
        while (!done) {
            if (!this.drawDeck.isEmpty()) {
                Card cardDrawn = this.drawDeck.poll();
                this.hand.add(cardDrawn);

                Card cardDiscarded = this.hand.poll();
                this.discardDeck.add(cardDiscarded);

                this.outputTurn(cardDrawn,cardDiscarded);
            }
            if (CardGame.winner.get() != -1) {
                this.stopThread();
                this.outputLossGame();
            }
            else if (this.hasWon()) {
                CardGame.winner.compareAndSet(-1, this.Id);
                System.out.println("Player "+ this.Id +" wins");
                this.stopThread();
                this.outputWinGame();
            }
        }
    }

    public void stopThread() {
        done = true;
    }
}

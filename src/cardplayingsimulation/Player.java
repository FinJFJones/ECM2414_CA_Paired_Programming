package cardplayingsimulation;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;


class Player extends Thread{
    private volatile ConcurrentLinkedQueue<Card> drawDeck;
    private volatile ConcurrentLinkedQueue<Card> discardDeck;

    private volatile boolean done = false;
    final private int Id;
    final private int numPlayers;
    final private PriorityQueue<Card> hand;
    final private String filePath;

    Player(int Id, ConcurrentLinkedQueue<Card> drawDeck, ConcurrentLinkedQueue<Card> discardDeck, int numPlayers) {
        this.Id = Id;
        this.numPlayers = numPlayers;
        this.hand = new PriorityQueue<>(
            (card1, card2) -> Boolean.compare(card1.getValue() == Id, card2.getValue() == Id)
        );

        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;

        this.filePath = "outputs/player" +this.Id + "_output.txt";
        OutputHandler.createTextFile(filePath);
    }

    void deal(Card card) {
        this.hand.add(card);
    }

    boolean hasWon() {
        Card firstCard = this.hand.peek();
        for (Card card: this.hand) {
            if (card.getValue() != firstCard.getValue()) {
                return false;
            }
        }
        return true;
    }

    String getFilePath(){
        return this.filePath;
    }

    int getPlayerId(){
        return this.Id;
    }

    String showHand(){
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
        OutputHandler.writeToTextFile(this.getFilePath(),drawnText,true);

        String discardText = "player " + playerId + " discards a " + cardDiscarded.getValue() + " to deck " + ((playerId%this.numPlayers)+1);
        OutputHandler.writeToTextFile(this.getFilePath(),discardText,true);

        String handText = "player " + playerId + " current hand is" + this.showHand();
        OutputHandler.writeToTextFile(this.getFilePath(),handText,true);
    }

    private void outputLossGame(){
        int playerId = this.getPlayerId();
        int winnerId = CardGame.winner.get();

        String line1 = "player " + winnerId + " has informed player " + playerId + " that player " + winnerId + " has won";
        String line2 = "player " + playerId + " exits";
        String line3 = "player " + playerId + " hand:" + this.showHand();

        String text = line1 + "\n" + line2 + "\n" + line3;
        OutputHandler.writeToTextFile(this.getFilePath(), text,false);
    }

    private void outputWinGame(){
        int playerId = this.getPlayerId();

        String line1 = "player " + playerId + " wins";
        String line2 = "player " + playerId + " exits";
        String line3 = "player " + playerId + " final hand:" + this.showHand();

        String text = line1 + "\n" + line2 + "\n" + line3;
        OutputHandler.writeToTextFile(this.getFilePath(), text,false);
    }

    @Override
    public void run() {
        while (!done) {
            if (CardGame.winner.get() != -1) {
                this.stopThread();
                this.outputLossGame();
            }
            else if (this.hasWon()) {
                if (CardGame.winner.compareAndSet(-1, this.Id)) {
                    System.out.println("Player "+ this.Id +" wins");
                    this.outputWinGame();
                } else {
                    this.outputLossGame();
                }
                this.stopThread();
            }
            else if (!this.drawDeck.isEmpty()) {
                Card cardDrawn = this.drawDeck.poll();
                this.hand.add(cardDrawn);

                Card cardDiscarded = this.hand.poll();
                this.discardDeck.add(cardDiscarded);

                this.outputTurn(cardDrawn,cardDiscarded);
            }
        }
    }

    void stopThread() {
        done = true;
    }
}

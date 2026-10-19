package cardplayingsimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Thread-safe Player class.
 * @author Thomas Russell and Finley Jones.
 */

class Player extends Thread {
    /**
     * Player class extends Thread, overridden run method for drawing/discarding.
     */
    private final ConcurrentLinkedQueue<Card> drawDeck;
    private final ConcurrentLinkedQueue<Card> discardDeck;

    private final ConcurrentLinkedQueue<Card> firstLock;
    private final ConcurrentLinkedQueue<Card> secondLock;

    private volatile boolean done = false;
    final private int Id;
    final private int numPlayers;
    final private Card[] hand;
    final private String filePath;

    /**
         * Player constructor.
         * @param Id int, player's ID 1 -> n.
         * @param drawDeck ConcurrentLinkedQueue<Card>, deck to be drawn from.
         * @param discardDeck ConcurrentLinkedQueue<Card>, deck to discard to.
         */
    Player(int Id, ConcurrentLinkedQueue<Card> drawDeck, ConcurrentLinkedQueue<Card> discardDeck, int numPlayers) {
        this.Id = Id;
        this.numPlayers = numPlayers;

        this.drawDeck = drawDeck;
        this.discardDeck = discardDeck;

        this.hand = new Card[4];

        String cwd = System.getProperty("user.dir");
        this.filePath = cwd+"/outputs/player" + this.Id + "_output.txt";
        OutputHandler.createTextFile(filePath);

        if(Id%numPlayers == 0){
            firstLock = discardDeck;
            secondLock = drawDeck;
        } else {
            firstLock = drawDeck;
            secondLock = discardDeck;
        }
    }
    /**
     * Deal card to hand.
     * @param card Card, the card to be added to the hand.
     * @param index int, index of the hand to add the card.
     */
    void deal(Card card, int index) {
        this.hand[index] = card;
    }
    /**
     * Checks if the player has won by evaluating all cards in hand.
     * @return boolean - true if player has won, false otherwise.
     */
    boolean hasWon() {
        Card firstCard = this.hand[0];
        for (Card card : this.hand) {
            if (card.getValue() != firstCard.getValue()) {
                return false;
            }
        }
        return true;
    }
    /**
     * Gets the filepath to where the player should write their outputs. 
     * @return String, the filepath.
     */
    String getFilePath() {
        return this.filePath;
    }
    /**
     * Gets the player's Id.
     * @return int, the Id.
     */
    int getPlayerId() {
        return this.Id;
    }
    /**
     * Gets the player's hand as a string in the format required by the specification.
     * @return String, the player's hand as a string.
     */
    String showHand() {
        String handString = "";
        for (Card card : this.hand) {
            handString += " " + card.getValue();
        }
        return handString;
    }
    /**
     * Outputs the result of a draw/discard cycle to the player's output file.
     * @param cardDrawn Card, the card that was drawn this cycle.
     * @param cardDiscarded Card, the card that was discarded this cycle.
     */
    private void outputTurn(Card cardDrawn, Card cardDiscarded) {
        int playerId = this.getPlayerId();

        String drawnText = "player " + playerId + " draws a " + cardDrawn.getValue() + " from deck " + playerId;
        OutputHandler.writeToTextFile(this.getFilePath(), drawnText, true);

        String discardText = "player " + playerId + " discards a " + cardDiscarded.getValue() + " to deck "
                + ((playerId % this.numPlayers) + 1);
        OutputHandler.writeToTextFile(this.getFilePath(), discardText, true);

        String handText = "player " + playerId + " current hand is" + this.showHand();
        OutputHandler.writeToTextFile(this.getFilePath(), handText, true);
    }
    /**
     * Final write to output file for if the player has lost the game.
     */
    private void outputLossGame() {
        int playerId = this.getPlayerId();
        int winnerId = CardGame.winner.get();

        String line1 = "player " + winnerId + " has informed player " + playerId + " that player " + winnerId
                + " has won";
        String line2 = "player " + playerId + " exits";
        String line3 = "player " + playerId + " hand:" + this.showHand();

        String text = line1 + "\n" + line2 + "\n" + line3;
        OutputHandler.writeToTextFile(this.getFilePath(), text, false);
    }
    /**
     * Final write to output file for if the player has won the game.
     */
    private void outputWinGame() {
        int playerId = this.getPlayerId();

        String line1 = "player " + playerId + " wins";
        String line2 = "player " + playerId + " exits";
        String line3 = "player " + playerId + " final hand:" + this.showHand();

        String text = line1 + "\n" + line2 + "\n" + line3;
        OutputHandler.writeToTextFile(this.getFilePath(), text, false);
    }

    /**
     * Atomically draws and discards from/to hand by locking decks.
     * @param i int, the index of the card to be discarded and replaced.
     */
    private void atomicDrawDiscard(int i){
        Card cardDrawn;
        Card cardDiscarded;
        Boolean successfulTurn = false;

        synchronized (this.firstLock) {
            synchronized (this.secondLock) {
                cardDrawn = this.drawDeck.poll();
                cardDiscarded = this.hand[i];
                if (cardDrawn != null){
                    this.hand[i] = cardDrawn;
                    discardDeck.add(cardDiscarded);
                    successfulTurn = true;
                }    
            }
        }
        if (successfulTurn){
            outputTurn(cardDrawn, cardDiscarded);
        }
    }

    /**    
     * Overrides Thread.run to play the player's draw/discard cycle and check if won or lost game.
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        while (!done) {
            if (CardGame.winner.get() != -1 && CardGame.winner.get() != this.Id) {
                this.stopThread();
                this.outputLossGame();
            }
            else if (this.hasWon()) {
                if (CardGame.winner.compareAndSet(-1, this.Id) | CardGame.winner.get() == this.Id) {
                    System.out.println("Player " + this.Id + " wins");
                    this.outputWinGame();
                } else {
                    this.outputLossGame();
                }
                this.stopThread();
            } else {
                List<Integer> discardIndexs = new ArrayList<>();

                for (int i=0 ; i<this.hand.length ; i++){
                    Card card = this.hand[i];
                    if (card.getValue() != this.getPlayerId()){
                        discardIndexs.add(i);
                    }
                }
                
                int randNum = ThreadLocalRandom.current().nextInt(discardIndexs.size());
                int discIndex = discardIndexs.get(randNum);

                this.atomicDrawDiscard(discIndex);
            }
        }
    }

    /**
     * Changes the done flag used within the thread to true.
     */
    void stopThread() {
        done = true;
    }
}

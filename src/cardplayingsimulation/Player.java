package cardplayingsimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

class Player extends Thread {
    private final ConcurrentLinkedQueue<Card> drawDeck;
    private final ConcurrentLinkedQueue<Card> discardDeck;

    private final ConcurrentLinkedQueue<Card> firstLock;
    private final ConcurrentLinkedQueue<Card> secondLock;

    private volatile boolean done = false;
    final private int Id;
    final private int numPlayers;
    final private Card[] hand;
    final private String filePath;

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

    void deal(Card card, int index) {
        this.hand[index] = card;
    }

    boolean hasWon() {
        Card firstCard = this.hand[0];
        for (Card card : this.hand) {
            if (card.getValue() != firstCard.getValue()) {
                return false;
            }
        }
        return true;
    }

    String getFilePath() {
        return this.filePath;
    }

    int getPlayerId() {
        return this.Id;
    }

    String showHand() {
        String handString = "";
        for (Card card : this.hand) {
            handString += " " + card.getValue();
        }
        return handString;
    }

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

    private void outputWinGame() {
        int playerId = this.getPlayerId();

        String line1 = "player " + playerId + " wins";
        String line2 = "player " + playerId + " exits";
        String line3 = "player " + playerId + " final hand:" + this.showHand();

        String text = line1 + "\n" + line2 + "\n" + line3;
        OutputHandler.writeToTextFile(this.getFilePath(), text, false);
    }

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

    void stopThread() {
        done = true;
    }
}

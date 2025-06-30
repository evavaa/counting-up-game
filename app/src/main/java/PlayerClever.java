import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

public class PlayerClever extends ComputerPlayer implements CardsPlayedListener {

    private Hand deckTracker;
    private int[] cardCount = new int[4];
    private CleverStrategy currStrat;
    private boolean isEarly = false;

    public PlayerClever(Deck deck) {
        super(deck);
        deckTracker = deck.toHand();
        // Initialize card count of each player
        for (int i=0; i<cardCount.length; i++) {
            cardCount[i] = 13;
        }
    }

    @Override
    public Card selectCard(Card lastCard, boolean isFirstCard) {
        if (isEarlyStage() && !isEarly) {
            currStrat = CleverStrategyFactory.getInstance().createStrategy(isEarlyStage());
            isEarly = true;
        }
        if (!isEarlyStage() && isEarly) {
            currStrat = CleverStrategyFactory.getInstance().createStrategy(isEarlyStage());
            isEarly = false;
        }
        return currStrat.selectBestCard(lastCard, isFirstCard, this);
    }

    private boolean isEarlyStage() {
        for (int count : this.cardCount) {
            if (count <= 5) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void notifyCardPlayed(Card cardPlayed, int playerIndex) {
        cardCount[playerIndex] --;
        deckTracker.getCardList().remove(cardPlayed);
    }

    @Override
    public void initDeckTracker() {
        for (Card card : this.getCardList()) {
            deckTracker.getCardList().remove(card);
        }
    }

    public Hand getDeckTracker() {
        return deckTracker;
    }
}
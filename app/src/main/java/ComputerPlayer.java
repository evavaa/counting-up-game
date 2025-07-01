import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;

import java.util.ArrayList;
import java.util.Random;

public abstract class ComputerPlayer extends Player {
    private static int instanceCounter = 0;
    Random random;

    public ComputerPlayer(Deck deck) {
        super(deck);
        // Use current time plus instance counter to ensure unique seeds for each
        // computer player
        int seed = (int) (System.currentTimeMillis() + (instanceCounter++ * 1000));
        this.random = new Random(seed);
    }

    // return a random card from the list
    public Card randomCard(ArrayList<Card> list) {
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    /**
     * Get all valid card based on the
     * 
     * @param lastCard
     * @param isFirstCard
     * @return
     */
    public ArrayList<Card> getValidCards(Card lastCard, boolean isFirstCard) {
        ArrayList<Card> validCards = new ArrayList<>();
        for (Card card : this.getCardList()) {
            if (isValid(lastCard, card, isFirstCard)) {
                validCards.add(card);
            }
        }
        return validCards;
    }
}

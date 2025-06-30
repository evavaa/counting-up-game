import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.Random;

public class PlayerRandom extends ComputerPlayer {

    public PlayerRandom(Deck deck) {
        super(deck);
    }

    @Override
    public Card selectCard(Card lastCard, boolean isFirstCard) {
        // get all valid cards
        ArrayList<Card> validCards = getValidCards(lastCard, isFirstCard);

        // pass if there is no valid card
        if (validCards.size() == 0) {
            return null;
        }
        // randomly choose one card from all valid cards
        return randomCard(validCards);
    }

}
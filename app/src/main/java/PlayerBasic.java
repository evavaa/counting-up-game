import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.Random;

public class PlayerBasic extends ComputerPlayer {
    public PlayerBasic(Deck deck) {
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

        // find the lowest rank in valid cards
        ArrayList<Card> lowestCards = new ArrayList<>();
        int rank = ((Rank)validCards.get(0).getRank()).getRankCardValue();

        for (Card card : validCards) {
            if (((Rank)card.getRank()).getRankCardValue() < rank) {
                lowestCards.clear();
                lowestCards.add(card);
                rank = ((Rank)card.getRank()).getRankCardValue();
            } else if (((Rank)card.getRank()).getRankCardValue() == rank) {
                lowestCards.add(card);
            }
        }

        // randomly choose a card with same rank
        return randomCard(lowestCards);
    }
}

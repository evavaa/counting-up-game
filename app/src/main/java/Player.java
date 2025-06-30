import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

public abstract class Player extends Hand {
    public Player(Deck deck) {
        super(deck);
    }
    public abstract Card selectCard(Card lastCard, boolean isFirstCard);

    public boolean isValid(Card lastCard, Card newCard, boolean isFirstCard) {
        if (isFirstCard) {
            return (newCard.getRank() == Rank.ACE && newCard.getSuit() == Suit.CLUBS);
        }
        if (lastCard == null) {
            return true;
        }
        if (lastCard.getSuit() == newCard.getSuit()) {
            return ((Rank)newCard.getRank()).getRankCardValue() > ((Rank)lastCard.getRank()).getRankCardValue();
        }
        return lastCard.getRank() == newCard.getRank();
    }

}

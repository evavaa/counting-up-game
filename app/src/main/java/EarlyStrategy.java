import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public class EarlyStrategy implements CleverStrategy {

    public Card selectBestCard(Card lastCard, boolean isFirstCard, PlayerClever player) {

        // get all valid cards
        ArrayList<Card> validCards = player.getValidCards(lastCard, isFirstCard);

        // pass if there is no valid card
        if (validCards.isEmpty()) {
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
        // if only one lowest rank cards possible, if not biggest,
        if (lowestCards.size() == 1) {
            Card myCard = lowestCards.get(0);
            if (!isUniqueBiggestInHand(myCard, player) || isInvulnerable(myCard, player)) {
                return myCard;
            }
            return null;
        } else {
            for (Card myCard : lowestCards) {
                if (isInvulnerable(myCard, player)) {
                    return myCard;
                }
            }
        }
        return player.randomCard(lowestCards);
    }


    /**
     * Check if the card is biggest in my hand
     * @param myCard the card to check if biggest
     * @return true if the card is biggest in my hand, otherwise false
     */
    private boolean isUniqueBiggestInHand(Card myCard, PlayerClever player) {
        ArrayList<Card> restOfCards = new ArrayList<>(player.getCardList());
        restOfCards.remove(myCard);
        for (Card card : restOfCards) {
            if (((Rank)card.getRank()).getRankCardValue() >=
                    ((Rank)myCard.getRank()).getRankCardValue()) {
                //System.out.println(((Rank)card.getRank()).getRankCardValue());
                return false;
            }
        }
        return true;
    }

}

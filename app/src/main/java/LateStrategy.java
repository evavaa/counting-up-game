import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public class LateStrategy implements CleverStrategy {

    public Card selectBestCard(Card lastCard, boolean isFirstCard, PlayerClever player) {

        // get all valid cards
        ArrayList<Card> validCards = player.getValidCards(lastCard, isFirstCard);

        // pass if there is no valid card
        if (validCards.isEmpty()) {
            return null;
        }

        // Prioritize the invulnerable cards
        ArrayList<Card> invulnerableCards = new ArrayList<>();
        for (Card card : validCards) {
            if (isInvulnerable(card, player)) {
                invulnerableCards.add(card);
            }
        }

        // If there are more than one invulnerable cards,
        // Play the one with less cards that it can play after
        if (invulnerableCards.size() != 0) {
            return cardMinSmallerCards(invulnerableCards, player);
        }

        // If there are no invulnerable cards, play the lowest rank card
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

        return cardMinSmallerCards(lowestCards, player);
    }

    /**
     * Return the number of cards that myCard can defeat
     * after this card.
     *
     * @return true if there are no card that can be played after this card, otherwise false
     */
    private int numSmallerCards(Card myCard, PlayerClever player) {
        int numSmaller = 0;
        for (Card otherCard : player.getDeckTracker().getCardList()) {
            if (player.isValid(otherCard, myCard, false)) {
                numSmaller ++;
            }
        }
        return numSmaller;
    }

    private Card cardMinSmallerCards(ArrayList<Card> cardsToChoose, PlayerClever player) {
        if (cardsToChoose.size() == 1) {
            return cardsToChoose.get(0);
        } else {
            Card cardToPlay = null;
            int min = CountingUpGame.DECK.getNumberOfCards();
            for (Card cardToChoose : cardsToChoose) {
                if (numSmallerCards(cardToChoose, player) < min) {
                    cardToPlay = cardToChoose;
                }
            }
            return cardToPlay;
        }
    }
}

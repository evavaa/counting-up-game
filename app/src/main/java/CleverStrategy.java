import ch.aplu.jcardgame.Card;

import java.util.ArrayList;

public interface CleverStrategy {
    Card selectBestCard(Card lastCard, boolean isFirstCard, PlayerClever player);

    /**
     * Check whether if there are other cards that can be played
     * after this card.
     *
     * @return true if there are no card that can be played after this card, otherwise false
     */
    default boolean isInvulnerable(Card myCard, PlayerClever player) {
        for (Card otherCard : player.getDeckTracker().getCardList()) {
            if (player.isValid(myCard, otherCard, false)) {

                return false;
            }
        }
        return true;
    }
}

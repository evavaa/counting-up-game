import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.GGKeyListener;

import java.awt.event.KeyEvent;

public class PlayerHuman extends Player implements GGKeyListener {
    private boolean waitingForPass = false;
    private Card selected = null;

    public PlayerHuman(Deck deck) {
        super(deck);
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) {
            selected = card;
            }
        };
        addCardListener(cardListener);
    }

    /**
     * Getter method
     * @return passSelected
     */
    public boolean isWaitingForPass() {
        return waitingForPass;
    }

    public void startSelection() {
        setTouchEnabled(true);
        waitingForPass = true;
    }

    @Override
    public Card selectCard(Card lastCard, boolean isFirstCard) {
        Card selection = selected;
        if (selection != null) {
            if (!isValid(lastCard, selection, isFirstCard)) {
                // add a warning message
                return null;
            } else {
                setTouchEnabled(false);
                waitingForPass = false;
            }
        }
        selected = null;
        return selection;
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (waitingForPass && keyEvent.getKeyChar() == '\n') {
            setTouchEnabled(false);
            waitingForPass = false;
        }
        return false;
    }

    @Override
    public boolean keyReleased(KeyEvent keyEvent) {
        return false;
    }


}

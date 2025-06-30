import ch.aplu.jcardgame.Card;

public interface CardsPlayedListener {
    void notifyCardPlayed(Card card, int PlayerIndex);

    void initDeckTracker();
}

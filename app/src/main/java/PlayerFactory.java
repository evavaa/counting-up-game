import java.util.Properties;

public class PlayerFactory {
    private static PlayerFactory instance = null;
    private CountingUpGame game;

    private PlayerFactory(CountingUpGame game) {
        this.game = game;
    }

    public static PlayerFactory getInstance(CountingUpGame game) {
        if (instance == null){
            instance = new PlayerFactory(game);
        }
        return instance;
    }

    public Player createPlayer(String playerType){
        switch (playerType) {
            case "human":
                PlayerHuman playerHuman = new PlayerHuman(CountingUpGame.DECK);
                game.addKeyListener(playerHuman);
                return playerHuman;
            case "random":
                return new PlayerRandom(CountingUpGame.DECK);
            case "basic":
                return new PlayerBasic(CountingUpGame.DECK);
            case "clever":
                PlayerClever playerClever = new PlayerClever(CountingUpGame.DECK);
                game.addCardsPlayedListener(playerClever);
                return playerClever;
        }
        return null;
    }
}

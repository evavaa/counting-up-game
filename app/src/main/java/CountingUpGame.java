// CountingUpGame.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class CountingUpGame extends CardGame {
    private static final int SEED = 30008; // 30008, 30009, 30007, 30006
    private static final Random RANDOM = new Random(SEED);
    private Properties properties;
    private StringBuilder logResult = new StringBuilder();
    private List<List<String>> playerAutoMovements = new ArrayList<>();
    private final static String VERSION = "1.0";
    public final static int NUM_PLAYERS = 4;
    public final static int NUM_START_CARDS = 13;
    private final static int HAND_WIDTH = 400;
    private final static int TRICK_WIDTH = 40;
    public final static Deck DECK = new Deck(Suit.values(), Rank.values(), "cover");
    private final static Location[] HAND_LOCATIONS = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
    private final static Location[] SCORE_LOCATIONS = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            // new Location(650, 575)
            new Location(575, 575)
    };
    private Actor[] scoreActors = {null, null, null, null};
    private final static Location TRICK_LOCATIONS = new Location(350, 350);
    private final static Location TEXT_LOCATION = new Location(350, 450);
    private int thinkingTime = 2000;
    private int delayTime = 600;
    private Player[] players;
    private Location hideLocation = new Location(-500, -500);

    private int[] scores = new int[NUM_PLAYERS];
    private int[] autoIndexplayers = new int [NUM_PLAYERS];
    private boolean isAuto = false;
    private Card selected;

    Font bigFont = new Font("Arial", Font.BOLD, 36);

    private ArrayList<CardsPlayedListener> cardsPlayedListeners = new ArrayList<>();


    public void setStatus(String string) {
        setStatusText(string);
    }

    private void initScore() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            scores[i] = 0;
            String text = "[" + String.valueOf(scores[i]) + "]";
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], SCORE_LOCATIONS[i]);
        }
    }

    private void calculateScoreEndOfRound(int player, List<Card> cardsPlayed) {
        int totalScorePlayed = 0;
        for (Card card: cardsPlayed) {
            Rank rank = (Rank) card.getRank();
            totalScorePlayed += rank.getScoreCardValue();
        }
        scores[player] += totalScorePlayed;
    }

    private void calculateNegativeScoreEndOfGame(int player, List<Card> cardsInHand) {
        int totalScorePlayed = 0;
        for (Card card: cardsInHand) {
            Rank rank = (Rank) card.getRank();
            totalScorePlayed -= rank.getScoreCardValue();
        }
        scores[player] += totalScorePlayed;
    }

    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        int displayScore = scores[player] >= 0 ? scores[player] : 0;
        String text = "P" + player + "[" + String.valueOf(displayScore) + "]";
        scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], SCORE_LOCATIONS[player]);
    }

    private void initGame() {
        players = new Player[NUM_PLAYERS];
        PlayerFactory playerFactory = PlayerFactory.getInstance(this);
        for (int i = 0; i < NUM_PLAYERS; i++) {
            String playerTypeKey = "players." + i;
            String playerType = this.properties.getProperty(playerTypeKey);
            players[i] = playerFactory.createPlayer(playerType);
        }

        dealingOut(players, NUM_PLAYERS, NUM_START_CARDS);

        // After dealing out, let the CardsPlayedListener initialize their deck tracker
        for (CardsPlayedListener player : cardsPlayedListeners) {
            player.initDeckTracker();
        }
        for (int i = 0; i < NUM_PLAYERS; i++) {
            players[i].sort(Hand.SortType.SUITPRIORITY, false);
        }

        // graphics
        RowLayout[] layouts = new RowLayout[NUM_PLAYERS];
        for (int i = 0; i < NUM_PLAYERS; i++) {
            layouts[i] = new RowLayout(HAND_LOCATIONS[i], HAND_WIDTH);
            layouts[i].setRotationAngle(90 * i);
            // layouts[i].setStepDelay(10);
            players[i].setView(this, layouts[i]);
            players[i].setTargetArea(new TargetArea(TRICK_LOCATIONS));
            players[i].draw();
        }
    }

    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list) {
        int x = RANDOM.nextInt(list.size());
        return list.get(x);
    }

    private Rank getRankFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        Integer rankValue = Integer.parseInt(rankString);
        for (Rank rank : Rank.values()) {
            if (rank.getRankCardValue() == rankValue) {
                return rank;
            }
        }
        return Rank.ACE;
    }

    private Suit getSuitFromString(String cardName) {
        String suitString = cardName.substring(cardName.length() - 1);
        for (Suit suit : Suit.values()) {
            if (suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }
        return Suit.CLUBS;
    }

    private Card getCardFromList(List<Card> cards, String cardName) {
        Rank cardRank = getRankFromString(cardName);
        Suit cardSuit = getSuitFromString(cardName);
        for (Card card: cards) {
            if (card.getSuit() == cardSuit
                    && card.getRank() == cardRank) {
                return card;
            }
        }
        return null;
    }

    private void dealingOut(Hand[] players, int NUM_PLAYERS, int nbCardsPerPlayer) {
        Hand pack = DECK.toHand(false);
        //int[] cardsDealtPerPlayer = new int[NUM_PLAYERS];

        for (int i = 0; i < NUM_PLAYERS; i++) {
            String initialCardsKey = "players." + i + ".initialcards";
            String initialCardsValue = properties.getProperty(initialCardsKey);
            if (initialCardsValue == null) {
                continue;
            }
            String[] initialCards = initialCardsValue.split(",");
            for (String initialCard: initialCards) {
                if (initialCard.length() <= 1) {
                    continue;
                }
                Card card = getCardFromList(pack.getCardList(), initialCard);
                if (card != null) {
                    card.removeFromHand(false);
                    players[i].insert(card, false);
                }
            }
        }

        for (int i = 0; i < NUM_PLAYERS; i++) {
            int cardsToDealt = nbCardsPerPlayer - players[i].getNumberOfCards();
            for (int j = 0; j < cardsToDealt; j++) {
                if (pack.isEmpty()) return;
                Card dealt = randomCard(pack.getCardList());
                dealt.removeFromHand(false);
                players[i].insert(dealt, false);
            }
        }
    }

    private int playerIndexWithAceClub() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            Player player = players[i];
            List<Card> cards = player.getCardsWithRank(Rank.ACE);
            if (cards.isEmpty()) {
                continue;
            }
            for (Card card: cards) {
                if (card.getSuit() == Suit.CLUBS) {
                    return i;
                }
            }
        }
        return 0;
    }

    private void addCardPlayedToLog(int player, Card selectedCard) {
        if (selectedCard == null) {
            logResult.append("P" + player + "-SKIP,");
        } else {
            Rank cardRank = (Rank) selectedCard.getRank();
            Suit cardSuit = (Suit) selectedCard.getSuit();
            logResult.append("P" + player + "-" + cardRank.getRankCardLog() + cardSuit.getSuitShortHand() + ",");
        }
    }

    private void addRoundInfoToLog(int roundNumber) {
        logResult.append("Round" + roundNumber + ":");
    }

    private void addEndOfRoundToLog() {
        logResult.append("Score:");
        for (int i = 0; i < scores.length; i++) {
            logResult.append(scores[i] + ",");
        }
        logResult.append("\n");
    }

    private void addEndOfGameToLog(List<Integer> winners) {
        logResult.append("EndGame:");
        for (int i = 0; i < scores.length; i++) {
            logResult.append(scores[i] + ",");
        }
        logResult.append("\n");
        logResult.append("Winners:" + String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList())));
    }

    private void playGame() {
        // End trump suit
        Hand playingArea = null;
        int winner = 0;
        int roundNumber = 1;
        for (int i = 0; i < NUM_PLAYERS; i++) updateScore(i);
        boolean isContinue = true;
        int skipCount = 0;
        Card lastCard = null;
        //List<Card>cardsPlayed = new ArrayList<>();
        playingArea = new Hand(DECK);
        addRoundInfoToLog(roundNumber);

        // Start with the player who has ace
        int nextPlayer = playerIndexWithAceClub();
        boolean isFirstCard = true;
        // While the game has not ended, loop over each player's turn
        while(isContinue) {
            selected = null;
            boolean finishedAuto = false;

            // If the game is in auto mode
            if (isAuto) {
                int nextPlayerAutoIndex = autoIndexplayers[nextPlayer];
                List<String> nextPlayerMovement = playerAutoMovements.get(nextPlayer);
                String nextMovement = "";

                if (nextPlayerMovement.size() > nextPlayerAutoIndex) {
                    nextMovement = nextPlayerMovement.get(nextPlayerAutoIndex);
                    nextPlayerAutoIndex++;

                    autoIndexplayers[nextPlayer] = nextPlayerAutoIndex;
                    Hand nextHand = players[nextPlayer];

                    if (nextMovement.equals("SKIP")) {
                        setStatusText("Player " + nextPlayer + " skipping...");
                        delay(thinkingTime);
                        selected = null;
                    } else {
                        setStatusText("Player " + nextPlayer + " thinking...");
                        delay(thinkingTime);
                        selected = getCardFromList(nextHand.getCardList(), nextMovement);
                    }
                } else {
                    // If finished all the auto steps set in property, end auto mode
                    finishedAuto = true;
                }
            }

            // If the game is not in auto mode, or if the auto testing ends,
            // continue using implemented logic
            if (!isAuto || finishedAuto){
                if (players[nextPlayer] instanceof PlayerHuman) {
                    ((PlayerHuman)players[nextPlayer]).startSelection();
                    setStatus("Player 0 double-click on card to follow or press Enter to pass");
                    while (null == selected && ((PlayerHuman)players[nextPlayer]).isWaitingForPass()) {
                        delay(delayTime);
                        selected = players[nextPlayer].selectCard(lastCard, isFirstCard);
                    }
                } else {
                    setStatusText("Player " + nextPlayer + " thinking...");
                    delay(thinkingTime);
                    selected = players[nextPlayer].selectCard(lastCard, isFirstCard);
                    if (selected == null) {
                        setStatusText("Player " + nextPlayer + " skipping...");
                        delay(thinkingTime);
                    }
                }
            }

            isFirstCard = false;
            if (selected != null) {
                // tell the listeners which card has been played
                notifyCardsPlayedListener(selected, nextPlayer);
            }

            // Follow with selected card

            playingArea.setView(this, new RowLayout(TRICK_LOCATIONS, (playingArea.getNumberOfCards() + 2) * TRICK_WIDTH));
            playingArea.draw();
            addCardPlayedToLog(nextPlayer, selected);
            if (selected != null) {
                lastCard = selected;
                skipCount = 0;
                //cardsPlayed.add(selected);
                selected.setVerso(false);  // In case it is upside down
                selected.transfer(playingArea, true); // transfer to trick (includes graphic effect)
                delay(delayTime);
                // End Follow
            } else {
                skipCount++;
            }

            if (skipCount == NUM_PLAYERS - 1) {
                playingArea.setView(this, new RowLayout(hideLocation, 0));
                playingArea.draw();
                winner = (nextPlayer + 1) % NUM_PLAYERS;
                skipCount = 0;
                lastCard = null;
                calculateScoreEndOfRound(winner, playingArea.getCardList());
                updateScore(winner);
                addEndOfRoundToLog();
                roundNumber++;
                addRoundInfoToLog(roundNumber);
                delay(delayTime);
                playingArea = new Hand(DECK);
            }

            isContinue = players[0].getNumberOfCards() > 0 && players[1].getNumberOfCards() > 0 &&
                    players[2].getNumberOfCards() > 0 && players[3].getNumberOfCards() > 0;
            if (!isContinue) {
                winner = nextPlayer;
                calculateScoreEndOfRound(winner, playingArea.getCardList());
                addEndOfRoundToLog();
            } else {
                nextPlayer = (nextPlayer + 1) % NUM_PLAYERS;
            }
            delay(delayTime);
        }

        for (int i = 0; i < NUM_PLAYERS; i++) {
            calculateNegativeScoreEndOfGame(i, players[i].getCardList());
            updateScore(i);
        }
    }

    private void setupPlayerAutoMovements() {
        String player0AutoMovement = properties.getProperty("players.0.cardsPlayed");
        String player1AutoMovement = properties.getProperty("players.1.cardsPlayed");
        String player2AutoMovement = properties.getProperty("players.2.cardsPlayed");
        String player3AutoMovement = properties.getProperty("players.3.cardsPlayed");

        String[] playerMovements = new String[] {"", "", "", ""};
        if (player0AutoMovement != null) {
            playerMovements[0] = player0AutoMovement;
        }

        if (player1AutoMovement != null) {
            playerMovements[1] = player1AutoMovement;
        }

        if (player2AutoMovement != null) {
            playerMovements[2] = player2AutoMovement;
        }

        if (player3AutoMovement != null) {
            playerMovements[3] = player3AutoMovement;
        }

        for (int i = 0; i < playerMovements.length; i++) {
            String movementString = playerMovements[i];
            List<String> movements = Arrays.asList(movementString.split(","));
            playerAutoMovements.add(movements);
        }
    }

    public String runApp() {
        setTitle("CountingUpGame (V" + VERSION + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScore();
        //addKeyListener(this);
        setupPlayerAutoMovements();
        initGame();
        playGame();

        for (int i = 0; i < NUM_PLAYERS; i++) updateScore(i);
        int maxScore = 0;
        for (int i = 0; i < NUM_PLAYERS; i++) if (scores[i] > maxScore) maxScore = scores[i];
        List<Integer> winners = new ArrayList<Integer>();
        for (int i = 0; i < NUM_PLAYERS; i++) if (scores[i] == maxScore) winners.add(i);
        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " +
                    winners.iterator().next();
        } else {
            winText = "Game Over. Drawn winners are players: " +
                    String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        addActor(new Actor("sprites/gameover.gif"), TEXT_LOCATION);
        setStatusText(winText);
        refresh();
        addEndOfGameToLog(winners);

        return logResult.toString();
    }

    public CountingUpGame(Properties properties) {
        super(700, 700, 30);
        this.properties = properties;
        isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));
        if (isAuto) {
            thinkingTime = 50;
            delayTime = 10;
        }
    }

    public void addCardsPlayedListener(CardsPlayedListener player) {
        cardsPlayedListeners.add(player);
    }

    public void notifyCardsPlayedListener(Card card, int playerIndex) {
        for (CardsPlayedListener player : cardsPlayedListeners) {
            player.notifyCardPlayed(card, playerIndex);
        }
    }

}

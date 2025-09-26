package model;

import model.board.BattleBoard;
import model.board.BoardLocation;
import model.board.GameBoard;
import model.cards.*;
import model.states.GameState;
import model.states.InitialGameState;
import view.ScreenHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Model {
    private final ScreenHandler screenHandler;
    private final GameData gameData;
    private GameState currentState;
    private boolean gameIsOver = false;

    public Model(ScreenHandler screenHandler) {
        this.screenHandler = screenHandler;
        gameData = new GameData();
        currentState = new InitialGameState();
    }

    public void runGameScript() {
        while (!gameIsOver()) {
            GameState nextState = currentState.run(this);
            currentState = nextState;
        }
    }

    private boolean gameIsOver() {
        return gameIsOver;
    }

    public ScreenHandler getScreenHandler() {
        return screenHandler;
    }

    public void setUpDecks() {
        gameData.rebelUnitDeck = new RebelUnitDeck();
        screenHandler.println("Shuffling " + gameData.rebelUnitDeck.size() +
                " Rebel Unit Cards into Rebel Forces Deck.");
        for (BattleBoard bb : gameData.gameBoard.getBattles()) {
            screenHandler.println("Adding 3 Rebel Units to " + bb.getName() + ".");
            for (int i = 0; i < 3; ++i) {
                bb.addRebelCard(gameData.rebelUnitDeck.drawOne());
            }
        }
        gameData.tacticsDeck = new TacticsDeck();
        screenHandler.println("Shuffling " + gameData.tacticsDeck.size() +
                " Tactics Cards into a deck.");
    }

    public void setUpBoards() {
        gameData.gameBoard = new GameBoard();
    }

    public void setUpTracks() {
        gameData.gameTracks = new GameTracks(this);
        gameData.gameTracks.printYourself(this.screenHandler);

    }

    public void setUpPlayers(int noOfPlayers) {
        gameData.players = new ArrayList<>();
        List<Player> allPlayers = new ArrayList<>(List.of(
                new Player(MyColors.YELLOW, "General Gold", 'G'),
                new Player(MyColors.GREEN, "General Jade", 'J'),
                new Player(MyColors.BROWN, "Admiral Ebony", 'E'),
                new Player(MyColors.WHITE, "Admiral Ivory", 'I'),
                new Player(MyColors.RED, "General Ruby", 'R'),
                new Player(MyColors.PURPLE, "Admiral Amethyst", 'A'),
                new Player(MyColors.PINK, "Admiral Opal", 'O'),
                new Player(MyColors.BLUE, "General Sapphire", 'S')
        ));
        Collections.shuffle(allPlayers);

        screenHandler.println("Preparing Alignment cards. Making a set with " + noOfPlayers +
                " Empire cards and 2 Rebel cards.");
        Deck<AlignmentCard> alignmentCards = new Deck<>();
        alignmentCards.addCopies(new EmpireAlignmentCard(), noOfPlayers);
        alignmentCards.addCopies(new RebelAlignmentCard(), 2);
        alignmentCards.shuffle();

        for (int i = 0; i < noOfPlayers; ++i) {
            Player p = allPlayers.removeFirst();
            screenHandler.println("Adding " + p.getNameWithShort() + " to the game.");
            screenHandler.println("  Placing standee on " + gameData.gameBoard.getCentralia().getName() + ".");
            p.moveToLocation(gameData.gameBoard.getCentralia());
            screenHandler.println("  Giving player " + p.getUnitDeck().size() + " Empire Unit cards, shuffled into a deck.");
            screenHandler.println("  Drawing a starting hand of 3 cards: ");
            p.drawUnitCards(this, 3);
            screenHandler.print("  ");
            p.printHand(screenHandler);

            AlignmentCard loyalty = alignmentCards.drawOne();
            screenHandler.println("  Giving player an Alignment card as loyalty: " + loyalty.getName() + ".");
            p.setLoyaltyCard(loyalty);
            gameData.players.add(p);
        }
        gameData.currentPlayer = gameData.players.getFirst();

        screenHandler.println("The remaining " + alignmentCards.size() +
                " Alignment cards, plus 2 of each alignment, become the Battle Chance deck.");
        alignmentCards.addCopies(new EmpireAlignmentCard(), 2);
        alignmentCards.addCopies(new RebelAlignmentCard(), 2);
        alignmentCards.shuffle();
        gameData.battleChanceDeck = alignmentCards;
    }

    public List<Player> getPlayers() {
        return gameData.players;
    }

    public int getTurn() {
        return gameData.gameTracks.getTurn();
    }

    public void drawBoard() {
        screenHandler.clearDrawingArea();
        gameData.gameBoard.drawYourself(this);
        gameData.gameTracks.drawYourself(this, 50, 10);
        screenHandler.printDrawingArea();
    }

    public Player getCurrentPlayer() {
        return gameData.currentPlayer;
    }

    public void stepCurrentPlayer() {
        int index = gameData.players.indexOf(gameData.currentPlayer);
        gameData.currentPlayer = gameData.players.get((index + 1) % gameData.players.size());
    }

    public RebelUnitDeck getRebelUnitDeck() {
        return gameData.rebelUnitDeck;
    }

    public BattleBoard[] getBattles() {
        return gameData.gameBoard.getBattles();
    }

    public void increaseUnrest(int i) {
        gameData.gameTracks.addToUnrest(i);
        if (gameData.gameTracks.isUnrestMaxedOut()) {
            screenHandler.println("Unrest has reached its maximum! A revolution starts on Centralia. " +
                    "Players with Rebel loyalty have won the game. Players with Empire loyalty have lost the game.");
            gameIsOver = true;
        }
    }

    public BoardLocation getCentralia() {
        return gameData.gameBoard.getCentralia();
    }

    public TacticsDeck getTacticsDeck() {
        return gameData.tacticsDeck;
    }

    public void resolveBattle(BattleBoard bb) {
        screenHandler.println("Resolving battle for " + bb.getName());
        bb.resolveYourself(this);
    }
}

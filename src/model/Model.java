package model;

import model.board.BattleBoard;
import model.board.BoardLocation;
import model.board.GameBoard;
import model.cards.*;
import model.states.GameState;
import model.states.InitialGameState;
import util.MyLists;
import util.MyPair;
import util.MyRandom;
import view.MultipleChoice;
import view.MultipleChoiceAction;
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
            addInitialRebelUnitsToBattle(bb);
        }
        gameData.tacticsDeck = new TacticsDeck();
        screenHandler.println("Shuffling " + gameData.tacticsDeck.size() +
                " Tactics Cards into a deck.");
    }

    private void addInitialRebelUnitsToBattle(BattleBoard bb) {
        screenHandler.println("Adding 3 Rebel Units to " + bb.getName() + ".");
        for (int i = 0; i < 3; ++i) {
            bb.addRebelCard(gameData.rebelUnitDeck.drawOne());
        }
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
        screenHandler.println("");
        screenHandler.clearDrawingArea();
        drawPlayers(gameData.players);
        gameData.gameBoard.drawYourself(this, 8, 2);
        drawDecks(20, 8);
        gameData.gameTracks.drawYourself(this, 48, 8);
        screenHandler.printDrawingArea();
        screenHandler.println("");
    }

    private void drawPlayers(List<Player> players) {
        List<MyPair<Integer, Integer>> bottomLocs = List.of(
                new MyPair<>(48, 14),
                new MyPair<>(24, 14),
                new MyPair<>(0, 14));
        for (int i = 0; i < 3 && i < players.size(); ++i) {
            players.get(i).drawYourselfHorizontally(this, bottomLocs.get(i).first, bottomLocs.get(i).second);
        }
        List<Integer> sideLocs = List.of(7, 0);
        for (int i = 3; i < 5 && i < players.size(); ++i) {
            players.get(i).drawYourselfVertically(this, 0, sideLocs.get(i-3));
        }

        List<MyPair<Integer, Integer>> topLocs = List.of(
                new MyPair<>(10, 0),
                new MyPair<>(34, 0));
        for (int i = 5; i < 7 && i < players.size(); ++i) {
            players.get(i).drawYourselfHorizontally(this, topLocs.get(i-5).first, topLocs.get(i-5).second);
        }
        if (players.size() == 8) {
            players.get(7).drawYourselfVertically(this, 64, 3);
        }
    }

    private void drawDecks(int x, int y) {
        screenHandler.drawText("Decks", x+5, y);
        getTacticsDeck().drawYourself(screenHandler, x, y+1);
        if (gameData.commonEmpireUnitDeck != null) {
            gameData.commonEmpireUnitDeck.drawYourself(screenHandler, x + 7, y + 1);
        }
        gameData.rebelUnitDeck.drawYourself(screenHandler, x + 14, y + 1);

        screenHandler.drawText("Discards", x+4, y+4);
        if (!gameData.tacticsDiscard.isEmpty()) {
            screenHandler.drawText("T " + gameData.tacticsDiscard.size(), x, y + 5);
        }
        if (!gameData.empireUnitDiscard.isEmpty()) {
            screenHandler.drawText("E " + gameData.empireUnitDiscard.size(), x + 7, y + 5);
        }
        if (!gameData.rebelUnitDiscard.isEmpty()) {
            screenHandler.drawText("R " + gameData.rebelUnitDiscard.size(), x + 14, y + 5);
        }
    }

    public Player getCurrentPlayer() {
        return gameData.currentPlayer;
    }

    public void stepCurrentPlayer() {
        int index = gameData.players.indexOf(gameData.currentPlayer);
        gameData.currentPlayer = gameData.players.get((index + 1) % gameData.players.size());
    }

    public RebelUnitCard drawRebelUnitCard() {
        if (gameData.rebelUnitDeck.isEmpty()) {
            for (RebelUnitCard c : gameData.rebelUnitDiscard) {
                gameData.rebelUnitDeck.addCard(c);
            }
            gameData.rebelUnitDeck.shuffle();
        }
        return gameData.rebelUnitDeck.drawOne();
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
        screenHandler.println("== Resolving " + bb.getName() + " ==");
        bb.resolveYourself(this);
        bb.movePlayersAfterBattle(this);
        BattleBoard newBattle = gameData.gameBoard.replaceBattle(this, bb);
        if (bb.getIdentifier() == newBattle.getIdentifier()) {
            screenHandler.println(bb.getName() + " is flipped to other side; " + newBattle.getName() + ".");
        } else {
            screenHandler.println(bb.getName() + " is obsolete. Replaced by " + newBattle.getName() + ".");
        }
        addInitialRebelUnitsToBattle(newBattle);
        drawBoard();
        for (BattleBoard chainedBattle : getBattles()) {
            if (chainedBattle.battleIsTriggered()) {
                resolveBattle(chainedBattle);
            }
        }
    }

    public void advanceWarCounter() {
        gameData.gameTracks.addToWar(+1);
    }

    public void retreatWarCounter() {
        gameData.gameTracks.addToWar(-1);
    }

    public int getUnrest() {
        return gameData.gameTracks.getUnrest();
    }

    public int getMaxUnrest() {
        return GameTracks.getMaxUnrest();
    }

    public void advanceHealthCounter(int i) {
        gameData.gameTracks.addToHealth(i);
        if (gameData.gameTracks.isEmperorDeath()) {
            // TODO: Emperor death ending.
        }
    }

    public int getEmperorHealth() {
        return gameData.gameTracks.getHealth();
    }

    public int getEmperorMaxHealth() {
        return gameData.gameTracks.getMaxHealth();
    }

    public void incrementTurn() {
        gameData.gameTracks.incrementTurn();
    }

    public EmpireUnitCard drawCommunalEmpireUnitCard() {
        if (gameData.commonEmpireUnitDeck == null) {
            gameData.commonEmpireUnitDeck = new CommonEmpireUnitDeck(gameData.empireUnitDiscard);
            gameData.empireUnitDiscard.clear();
        }
        return gameData.commonEmpireUnitDeck.drawOne();
    }

    public void discardRebelCards(List<RebelUnitCard> cards) {
        gameData.rebelUnitDiscard.addAll(cards);
    }

    public void discardEmpireCards(List<EmpireUnitCard> cards) {
        gameData.empireUnitDiscard.addAll(cards);
    }

    public void discardTacticsCards(List<TacticsCard> cards) {
        gameData.tacticsDiscard.addAll(cards);
    }

    public AlignmentCard drawBattleChanceCard() {
        AlignmentCard cardToReturn = gameData.battleChanceDeck.drawOne();
        gameData.battleChanceDeck.addCard(cardToReturn);
        gameData.battleChanceDeck.shuffle();
        return cardToReturn;
    }

    public int getWarCounter() {
        return gameData.gameTracks.getWar();
    }

    public boolean checkForBattleOfCentralia() {
        return gameData.gameTracks.isBattleOfCentralia();
    }

    public boolean checkForBattleAtRebelStronghold() {
        return gameData.gameTracks.isBattleAtTheRebelStronghold();
    }

    public void resetWarCounter() {
        gameData.gameTracks.setWarCounter(0);
    }

    public void setGameOver(boolean b) {
        gameIsOver = b;
    }

    public BattleBoard makeBattleBoardReplacement(Model model, BattleBoard bb) {
        return gameData.gameTracks.replaceBattleBoard(model, bb);
    }
}

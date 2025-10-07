package model;

import model.board.BattleBoard;
import model.board.BoardLocation;
import model.board.GameBoard;
import model.cards.*;
import model.cards.alignment.AlignmentCard;
import model.cards.alignment.EmpireAlignmentCard;
import model.cards.alignment.RebelAlignmentCard;
import model.cards.events.EventCard;
import model.cards.events.EventDeck;
import model.cards.tactics.TacticsCard;
import model.cards.tactics.TacticsDeck;
import model.cards.units.*;
import model.states.GameState;
import model.states.InitialGameState;
import util.Arithmetics;
import util.MyLists;
import util.MyPair;
import view.MultipleChoice;
import view.ScreenHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Model {
    private final ScreenHandler screenHandler;
    private GameData gameData;
    private boolean gameIsOver = false;

    public Model(ScreenHandler screenHandler) {
        this.screenHandler = screenHandler;
        gameData = new GameData();
    }

    public void runGameScript() {
        askToLoadGame();
        try {
            while (!gameIsOver()) {
                GameState nextState = gameData.currentState.run(this);
                gameData.currentState = nextState;
                saveGame();
            }
        } catch (GameOverException goe) {
            // Intentional fall through.
        }
        handleGameEnding();
    }

    private void askToLoadGame() {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption("New game", (_,_) -> {});
        multipleChoice.addOption("Load game", (m, p) -> {loadGameFromSave();});
        multipleChoice.promptAndDoAction(this, "Welcome to Space Royale 2 Simulator!", null);
    }


    public void saveGame() {
        getScreenHandler().println("* Saving Game *");
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("save_data.sr2"));
            oos.writeObject(gameData);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void loadGameFromSave() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("save_data.sr2"));
            gameData = (GameData) ois.readObject();
            ois.close();
            getScreenHandler().println("* Game Loaded *");
            drawBoard();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGameEnding() {
        screenHandler.print("Game has ended, type of ending: ");
        if (gameData.gameTracks.isEmperorDeath()) {
            screenHandler.println("Emperor Death");
            printWinnersAndLosers(EmperorDeathEnding.makeWinCondition(this));
        } else if (gameData.gameTracks.getTurn() == GameTracks.MAX_GAME_TURN) {
            if (gameData.gameTracks.isEmperorHealthy()) {
                screenHandler.println("Default Ending (turn 8 ending, Emperor healthy)");
                printWinnersAndLosers(p -> p.getLoyaltyCard() instanceof EmpireAlignmentCard);
            } else { // Emperor health in decline (Can't be death, handled above)
                screenHandler.println("Succession Ending (turn 8 ending, Emperor decline)");
                printWinnersAndLosers(SuccessionEnding.makeWinCondition(this));
            }
        } else if (gameData.gameTracks.isUnrestMaxedOut()) {
            screenHandler.println("Revolution (unrest = max)");
            printWinnersAndLosers(p -> p.getLoyaltyCard() instanceof RebelAlignmentCard || p instanceof DefectedPlayer);
        } else if (gameData.gameTracks.isBattleOfCentralia()) {
            screenHandler.println("Defeat at Battle of Centralia");
            printWinnersAndLosers(p -> p.getLoyaltyCard() instanceof RebelAlignmentCard || p instanceof DefectedPlayer);
        } else if (gameData.gameTracks.isBattleAtTheRebelStronghold()) {
            screenHandler.println("Victory at Battle at the Rebel Stronghold");
            printWinnersAndLosers(p -> p.getLoyaltyCard() instanceof EmpireAlignmentCard);
        } else {
            drawBoard();
            System.err.println("Unknown win condition!");
        }
    }

    private void printWinnersAndLosers(WinCondition condition) {
        for (Player p : getPlayers()) {
            if (condition.hasWonGame(p)) {
                screenHandler.println(p.getName() + " is a winner.");
            } else {
                screenHandler.println(p.getName() + " is a loser.");
            }
        }
    }

    public boolean gameIsOver() {
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
        gameData.eventDeck = new EventDeck();
        screenHandler.println("Shuffling " + gameData.eventDeck.size() + " Event cards into a deck.");
    }

    private void addInitialRebelUnitsToBattle(BattleBoard bb) {
        screenHandler.println("Adding " + gameData.initialRebelUnitRate + " Rebel Units to " + bb.getName() + ".");
        for (int i = 0; i < gameData.initialRebelUnitRate; ++i) {
            try {
                bb.addRebelCard(drawRebelUnitCard());
            } catch (DeckIsEmptyException die) {
                screenHandler.println("Rebel deck is empty! Added " + i + " cards instead.");
                break;
            }
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
                " Empire cards and " + (noOfPlayers < 8 ? 2 : 3) + " Rebel cards.");
        BattleChanceDeck alignmentCards = new BattleChanceDeck();
        alignmentCards.addCopies(new EmpireAlignmentCard(), noOfPlayers);
        alignmentCards.addCopies(new RebelAlignmentCard(), 2);
        if (noOfPlayers == 8) {
            alignmentCards.addCard(new RebelAlignmentCard());
        }
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
        gameData.gameBoard.drawYourself(this, 8, 2);
        drawDecks(22, 8);
        drawPlayers(gameData.players);
        gameData.gameTracks.drawYourself(this, 54, 10);
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
            players.get(7).drawYourselfVertically(this, 64, 3, true);
        }
    }

    private void drawDecks(int x, int y) {
        screenHandler.drawText("<- DECKS ->", x+6, y);

        gameData.battleChanceDeck.drawYourself(screenHandler, x - 2, y - 1);
        getTacticsDeck().drawYourself(screenHandler, x+3, y+1);
        if (gameData.commonEmpireUnitDeck != null) {
            gameData.commonEmpireUnitDeck.drawYourself(screenHandler, x + 9, y + 1);
        }
        gameData.rebelUnitDeck.drawYourself(screenHandler, x + 16, y + 1);
        gameData.eventDeck.drawYourself(screenHandler, x + 20, y - 1);

        screenHandler.drawText("DISCARDS", x+8, y+4);
        if (!gameData.tacticsDiscard.isEmpty()) {
            screenHandler.drawText("T " + gameData.tacticsDiscard.size(), x+3, y + 5);
        }
        if (!gameData.empireUnitDiscard.isEmpty()) {
            screenHandler.drawText("E " + gameData.empireUnitDiscard.size(), x + 10, y + 5);
        }
        if (!gameData.rebelUnitDiscard.isEmpty()) {
            screenHandler.drawText("R " + gameData.rebelUnitDiscard.size(), x + 16, y + 5);
        }
        if (!gameData.eventDiscard.isEmpty()) {
            screenHandler.drawText("Ev" + gameData.eventDiscard.size(), x + 22, y + 5);
            screenHandler.drawText("nt", x + 22, y + 6);
        }

        if (!gameData.eventCardsInPlay.isEmpty()) {
            screenHandler.drawText("Events in play:", 54, 0);
            int yOff = 0;
            for (EventCard ev : gameData.eventCardsInPlay) {
                screenHandler.drawText(ev.getName(), 70, yOff++);
            }
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
            gameData.rebelUnitDiscard.clear();
        }
        if (gameData.rebelUnitDeck.isEmpty()) {
            throw new DeckIsEmptyException();
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
            throw new GameOverException();
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
        if (gameData.commonEmpireUnitDeck == null || gameData.commonEmpireUnitDeck.isEmpty()) {
            gameData.commonEmpireUnitDeck = new CommonEmpireUnitDeck(gameData.empireUnitDiscard);
            gameData.empireUnitDiscard.clear();
        }
        if (gameData.commonEmpireUnitDeck.isEmpty()) {
            throw new DeckIsEmptyException();
        }
        return gameData.commonEmpireUnitDeck.drawOne();
    }

    public void discardRebelCards(List<RebelUnitCard> cards) {
        gameData.rebelUnitDiscard.addAll(cards);
    }

    public void discardEmpireCards(List<EmpireUnitCard> cards) {
        gameData.empireUnitDiscard.addAll(cards);
    }

    public void discardTacticsCards(TacticsCard card) {
        gameData.tacticsDiscard.add(card);
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

    public BoardLocation getPrisonPlanet() {
        return gameData.gameBoard.getPrisonPlanet();
    }

    public EventCard drawEventCard() {
        if (gameData.eventDeck.isEmpty()) {
            if (gameData.eventDiscard.isEmpty()) {
                throw new DeckIsEmptyException();
            }
            gameData.eventDeck = new EventDeck(gameData.eventDiscard);
            gameData.eventDiscard.clear();
        }
        return gameData.eventDeck.drawOne();
    }

    public void putEventBackOnBottom(EventCard card) {
        gameData.eventDeck.putOnBottom(card);
    }

    public void discardEventCard(EventCard card) {
        gameData.eventDiscard.add(card);
    }

    public void incrementInitialRebelUnitRate() {
        gameData.initialRebelUnitRate++;
    }

    public int getInitialRebelUnitRate() {
        return gameData.initialRebelUnitRate;
    }

    public void setUnrest(int i) {
        gameData.gameTracks.setUnrest(i);
    }

    public List<Player> getPlayersStartingFrom(Player initiator) {
        List<Player> players = new ArrayList<>();
        int index = getPlayers().indexOf(initiator);
        for (int i = 0; i < getPlayers().size(); ++i) {
            players.add(getPlayers().get(index));
            index = Arithmetics.incrementWithWrap(index, getPlayers().size());
        }
        return players;
    }

    public void addBattleChanceCard(AlignmentCard card) {
        gameData.battleChanceDeck.addCard(card);
        gameData.battleChanceDeck.shuffle();
    }

    public List<EventCard> getEventCardsInPlay() {
        return gameData.eventCardsInPlay;
    }

    public void setEmperorHealth(int health) {
        gameData.gameTracks.setEmperorHealth(health);
    }

    public TacticsCard drawTacticsCard() {
        if (getTacticsDeck().isEmpty()) {
            if (gameData.tacticsDiscard.isEmpty()) {
                throw new DeckIsEmptyException();
            }
            gameData.tacticsDeck = new TacticsDeck(gameData.tacticsDiscard);
            gameData.tacticsDiscard.clear();
            screenHandler.println("The tactics deck is reshuffled.");
        }
        return getTacticsDeck().drawOne();
    }

    public AlignmentCard peekAtBattleChanceCard() {
        return gameData.battleChanceDeck.peek();
    }

    public void playerDefects(Player player) {
        screenHandler.println(player.getName() + " defects to the rebel side!");
        screenHandler.println(player.getShortName() + "'s Rebel Alignment card is placed in the Battle Chance deck.");
        addBattleChanceCard(player.getLoyaltyCard());
        DefectedPlayer defector = new DefectedPlayer(player);
        if (player.getCurrentLocation() == getPrisonPlanet()) {
            getScreenHandler().println("Since " + player.getName() +
                    " is on the Prison planet, no special Rebel Unit cards can be claimed.");
        } else {
            defector.tradeInUnits(this, player);
        }
        getScreenHandler().println(player.getShortName() + " discards " + player.getUnitCardsInHand().size() + " Unit cards.");
        for (EmpireUnitCard eu : new ArrayList<>(player.getUnitCardsInHand())) {
            player.discardCard(this, eu);
        }
        getScreenHandler().println(player.getShortName() + " discards " + player.getTacticsCardsInHand().size() + " Tactics cards.");
        for (TacticsCard tc : new ArrayList<>(player.getTacticsCardsInHand())) {
            player.discardCard(this, tc);
        }
        while (!player.getUnitDeck().isEmpty()) {
            discardEmpireCards(List.of(player.getUnitDeck().drawOne()));
        }

        for (int i = 0; i < 2; ++i) {
            defector.addCardToHand(gameData.rebelUnitDeck.drawOne());
        }
        getScreenHandler().println(player.getShortName() + " draws 2 Rebel Units:");
        defector.printHand(screenHandler);

        defector.drawEventCards(this);
        getScreenHandler().println(player.getShortName() + " is moved to Rebel Stronghold.");
        player.moveToLocation(gameData.gameBoard.getRebelStronghold());
        defector.moveToLocation(gameData.gameBoard.getRebelStronghold());

        gameData.players.set(gameData.players.indexOf(player), defector);
    }

    public List<Player> getPlayersNotDefectors() {
        return MyLists.filter(getPlayers(), p -> !(p instanceof DefectedPlayer));
    }

    public boolean hasCollaborativelyDrawnThisTurn(Player current) {
        return gameData.collaborativeDraw.contains(current);
    }

    public void resetCollaborativeDraw() {
        gameData.collaborativeDraw = new HashSet<>();
    }

    public void addToCollaborativeDrawers(Player performer) {
        gameData.collaborativeDraw.add(performer);
    }
}

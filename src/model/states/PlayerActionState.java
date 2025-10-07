package model.states;

import model.DefectedPlayer;
import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.board.BoardLocation;
import model.board.PrisonPlanetLocation;
import model.cards.DeckIsEmptyException;
import model.cards.alignment.RebelAlignmentCard;
import model.cards.events.EventCard;
import model.cards.tactics.TacticsCard;
import model.cards.units.*;
import util.MyLists;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public class PlayerActionState extends GameState {
    private static final int MAX_HAND_SIZE = 8;

    @Override
    public GameState run(Model model) {
        Player current = model.getCurrentPlayer();
        int turnsToPlay = model.getPlayers().size() - model.getPlayers().indexOf(current);
        model.getScreenHandler().println("Player Action step, turns to take: " + turnsToPlay);
        for (int i = 0; i < turnsToPlay; ++i) {
            current = model.getCurrentPlayer();
            println(model, current.getName() + "'s turn. ");
            println(model, "Location: " + current.getCurrentLocation().getName() + ".");
            println(model, "Loyalty: " + current.getLoyaltyCard().getName());
            println(model, "Cards in hand (" + current.getTotalCardsInHand() + "):");
            current.printHand(model.getScreenHandler());
            if (current.takeTurn(model, this)) {
                break;
            }
            model.stepCurrentPlayer();
            model.drawBoard();
            if (i < turnsToPlay - 1) { // No need to save after last player's actions
                model.saveGame();      // Will automatically save after this state is done
            }
        }
        return new EmperorHealthDeclineState();
    }

    public boolean takeNormalPlayerTurn(Model model, Player current) {
        if (!isOnPrisonPlanet(current)) {
            doNegativeAction(model, current);
        }
        if (model.gameIsOver()) {
            return true;
        }
        doPlayerAction(model, current);
        if (model.getPlayers().contains(current)) { // player hasn't defected
            if (model.gameIsOver()) {
                return true;
            }
            drawThreeCards(model, current);
            discardIfOverLimit(model, current);
        }
        return false;
    }

    private void doNegativeAction(Model model, Player current) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption("Draw 2 Rebel Units", this::draw2RebelUnits);
        multipleChoice.addOption("Increase Unrest", this::increaseUnrest);
        multipleChoice.addOption("Draw Event Cards", this::drawEventCards);
        multipleChoice.promptAndDoAction(model, "Select a negative action:", current);
        checkForTriggeredBattles(model, current);
    }

    public void draw2RebelUnits(Model model, Player performer) {
        println(model, performer.getName() + " draws 2 Rebel Units from the deck.");
        List<RebelUnitCard> cards = new ArrayList<>();
        try {
            cards.add(model.drawRebelUnitCard());
            cards.add(model.drawRebelUnitCard());
        } catch (DeckIsEmptyException die) {
            model.getScreenHandler().println("Rebel deck is empty. Placing " + cards.size() + " cards.");
        }

        for (int i = 0; i < cards.size(); ++i) {
            String prompt = "Where would you like to place the " +
                    (i == 0 ? "first":"second") + " card?";
            MultipleChoice multipleChoice = new MultipleChoice();
            for (BattleBoard bb : model.getBattles()) {
                int finalI = i;
                multipleChoice.addOption(bb.getName(),
                        (_, _) -> {
                            bb.addRebelCard(cards.get(finalI));
                        });
            }
            multipleChoice.promptAndDoAction(model, prompt, performer);
        }
    }

    public static void checkForTriggeredBattles(Model model, Player player) {
        MultipleChoice multipleChoice = new MultipleChoice();
        for (BattleBoard bb : model.getBattles()) {
            if (bb.battleIsTriggered()) {
                multipleChoice.addOption(bb.getName(), (Model m, Player p) -> {
                    m.resolveBattle(bb);
                });
            }
        }
        if (multipleChoice.getNumberOfChoices() > 0) {
            model.getScreenHandler().println("There are multiple battles which need to be resolved.");
            multipleChoice.promptAndDoAction(model, "Which one would you like to resolve first?", player);
        }
    }

    private void increaseUnrest(Model model, Player player) {
        model.increaseUnrest(1);
        println(model, "Unrest is now at " + model.getUnrest() + "/" + model.getMaxUnrest());
    }

    private void drawEventCards(Model model, Player player) {
        EventCard card1 = model.drawEventCard();
        EventCard card2 = model.drawEventCard();
        println(model, "Drew " + card1.getNameAndMandatory() + " and " + card2.getNameAndMandatory() + ".");
        if (card1.isMandatory()) {
            resolveEventThenDiscard(model, player, card1);
            resolveEventThenDiscard(model, player, card2);
        } else if (card2.isMandatory()) {
            resolveEventThenDiscard(model, player, card2);
            resolveEventThenDiscard(model, player, card1);
        } else {
            MultipleChoice multipleChoice = new MultipleChoice();
            multipleChoice.addOption(card1.getName() + ": " + card1.getDescription(), (m, p) -> {
                playOneAndPutOtherBack(m, p, card1, card2);

            });
            multipleChoice.addOption(card2.getName() + ": " + card2.getDescription(), (m, p) -> {
                playOneAndPutOtherBack(m, p, card2, card1);
            });
            multipleChoice.promptAndDoAction(model, "Which card do you want to play?", player);
        }
    }

    private void playOneAndPutOtherBack(Model model, Player player, EventCard card1, EventCard card2) {
        model.putEventBackOnBottom(card2);
        resolveEventThenDiscard(model, player, card1);
    }

    private void resolveEventThenDiscard(Model model, Player player, EventCard card1) {
        model.getScreenHandler().println("Playing event " + card1.getName() + ".");
        card1.resolve(model, player);
        if (!card1.staysInPlay()) {
            model.discardEventCard(card1);
        } else {
            model.getEventCardsInPlay().add(card1);
            println(model, card1.getName() + " stays in play.");
        }
    }

    private void doPlayerAction(Model model, Player current) {
        MultipleChoice multipleChoice = new MultipleChoice();
        if (!isOnPrisonPlanet(current)) {
            multipleChoice.addOption("Move", this::movePlayer);
        }
        addNonMoveActions(multipleChoice, model, current);
        multipleChoice.promptAndDoAction(model, "Select an action for " + current.getName() + ":", current);
        if (!model.getPlayers().contains(current)) {
            return; // Player defected
        }

        if (multipleChoice.getSelectedOptionName().equals("Move")) {
            MultipleChoice multipleChoice2 = new MultipleChoice();
            addNonMoveActions(multipleChoice2, model, current);
            multipleChoice2.promptAndDoAction(model, "Select an action for " + current.getName() + ":", current);
        } else if (!multipleChoice.getSelectedOptionName().equals("Pass")) {
            MultipleChoice multipleChoice2 = new MultipleChoice();
            if (!isOnPrisonPlanet(current)) {
                multipleChoice2.addOption("Move", this::movePlayer);
            }
            multipleChoice2.addOption("Stay in location", (_, _) -> {});
            multipleChoice2.promptAndDoAction(model, "Does " + current.getName() + " want to move from " +
                    current.getCurrentLocation().getName() + "?", current);
        }
    }

    private void addNonMoveActions(MultipleChoice multipleChoice, Model model, Player current) {
        if (isOnCentralia(model, current)) {
            if (!model.hasCollaborativelyDrawnThisTurn(current)) {
                multipleChoice.addOption("Collaborative Draw Unit Cards", this::collaborativeDrawUnitCards);
            }
            if (model.getUnrest() > 0 && MyLists.filter(model.getPlayers(), p -> p.getCurrentLocation() == model.getCentralia()).size() > 1) {
                multipleChoice.addOption("Quell Unrest", QuellUnrestAction::doAction);
            }
            if (ArrestAction.anybodyArrestable(model, current)) {
                multipleChoice.addOption("Attempt Arrest", ArrestAction::arrestAction);
            }
            if (ArrestAction.anybodyOnPrisonPlanet(model)) {
                multipleChoice.addOption("Release from prison", ArrestAction::releaseFromPrison);
            }
        } else if (isOnPrisonPlanet(current)) {
            multipleChoice.addOption("Escape prison", ArrestAction::escapeFromPrison);
        } else if (!current.getUnitCardsInHand().isEmpty()) {
            multipleChoice.addOption("Add Cards to Battle", PlayerActionState::addCardsToBattle);
        }
        UnitCard agent = MyLists.find(current.getUnitCardsInHand(), eu -> eu instanceof AgentUnitCard);
        if (agent != null) {
            multipleChoice.addOption("Play Agent", (m, p) -> {
                ((AgentUnitCard)agent).playAsAction(m, p);
            });
        }
        if (current.getLoyaltyCard() instanceof RebelAlignmentCard) {
            multipleChoice.addOption("Defect", (m, p) -> {
                m.playerDefects(p);
            });
        }
        multipleChoice.addOption("Pass", (_, _) -> {});
    }

    private boolean isOnPrisonPlanet(Player current) {
        return current.getCurrentLocation() instanceof PrisonPlanetLocation;
    }

    private void notYetImplemented(Model model, Player player) {
        println(model, "Not yet implemented!");
    }

    public static void addCardsToBattle(Model model, Player player) {
        BattleBoard bb = (BattleBoard) player.getCurrentLocation();
        EmpireUnitCard agent = MyLists.find(player.getUnitCardsInHand(), eu -> eu instanceof AgentUnitCard);
        if (agent != null) {
            model.getScreenHandler().println(player.getName() + " has an Agent card and can play it to look at the Rebel Unit Cards on " + bb.getName() + ".");
            MultipleChoice multipleChoice = new MultipleChoice();
            multipleChoice.addOption("Use Agent", (m, _) -> ((AgentUnitCard)agent).useToPeek(m, player, bb));
            multipleChoice.addOption("Skip", (_, _) -> {});
            multipleChoice.promptAndDoAction(model, "Do you want to play an Agent Card?", player);
        }

        boolean[] done = new boolean[]{false};
        do {
            MultipleChoice multipleChoice = new MultipleChoice();
            for (EmpireUnitCard eu : player.getUnitCardsInHand()) {
                multipleChoice.addOption(eu.getNameAndStrength(), (_, performer) -> {
                    bb.addEmpireUnit(eu);
                    performer.removeUnitCardFromHand(eu);
                });
            }
            multipleChoice.addOption("Done", (_,_) -> { done[0] = true; });
            multipleChoice.promptAndDoAction(model, "Which card do you wish " + player.getName() + " to add to the battle?", player);
        } while (!done[0] && !player.getUnitCardsInHand().isEmpty());
        if (bb.battleIsTriggered()) {
            model.resolveBattle(bb);
        }
    }

    private void movePlayer(Model model, Player player) {
        MultipleChoice multipleChoice = new MultipleChoice();
        List<BoardLocation> destinations = new ArrayList<>();
        for (BattleBoard bb : model.getBattles()) {
            destinations.add(bb);
        }
        if (!isOnCentralia(model, player)) {
            destinations.add(model.getCentralia());
        }
       for (BoardLocation dest : destinations) {
           if (dest != player.getCurrentLocation()) {
               multipleChoice.addOption(dest.getName(),
                       (_, performer) -> {
                           BoardLocation movedFrom = performer.getCurrentLocation();
                           performer.moveToLocation(dest);
                           println(model, performer.getName() + " moves to " + dest.getName() + ".");
                           ShuttleCard.moveWithPlayer(model, performer, movedFrom, dest);
                       });
           }
        }
       multipleChoice.addOption("Cancel", (_, _) -> {});
        multipleChoice.promptAndDoAction(model, "Move to which location?", player);
    }

    private boolean isOnCentralia(Model model, Player player) {
        return model.getCentralia() == player.getCurrentLocation();
    }

    private void collaborativeDrawUnitCards(Model model, Player player) {
        MultipleChoice multipleChoice = new MultipleChoice();
        for (Player p : model.getPlayers()) {
            if (p != player && isOnCentralia(model, p) && !model.hasCollaborativelyDrawnThisTurn(p)) {
                multipleChoice.addOption(p.getName(), (model1, performer) -> drawCardsTogetherWith(model1, performer, p));
            }
        }
        if (multipleChoice.getNumberOfChoices() == 0) {
            drawCardsTogetherWith(model, player, null);
        } else {
            multipleChoice.promptAndDoAction(model, "Which player do you want collaboratively draw cards with?", player);
        }
    }

    private void drawCardsTogetherWith(Model model, Player performer, Player other) {
        drawTwoCards(model, performer);
        model.addToCollaborativeDrawers(performer);
        if (other != null) {
            drawTwoCards(model, other);
            model.addToCollaborativeDrawers(other);
            println(model, "It's still " + performer.getName() + "'s turn.");
        }
    }

    private void drawThreeCards(Model model, Player performer) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption("3 Unit Cards", (model1, performer1) -> performer1.drawUnitCards(model1, 3));
        if (isOnCentralia(model, performer) && !model.getTacticsDeck().isEmpty()) {
            multipleChoice.addOption("2 Unit Cards + 1 Tactics Card", (model2, performer2) -> {
                performer2.drawUnitCards(model2, 2);
                performer2.drawTacticsCard(model2);
            });
        }
        if (multipleChoice.getNumberOfChoices() > 1) {
            println(model, performer.getName() + " is about to draw cards.");
        } else {
            println(model, performer.getName() + " draws 3 cards.");
        }
        multipleChoice.promptAndDoAction(model, "Select an option:", performer);
        println(model, performer.getName() + "'s hand: ");
        performer.printHand(model.getScreenHandler());
    }

    private void drawTwoCards(Model model, Player performer) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption("2 Unit Cards", (model1, performer1) -> performer1.drawUnitCards(model1, 2));
        if (!model.getTacticsDeck().isEmpty()) {
            multipleChoice.addOption("1 Tactics Card", (model2, performer2) -> {
                performer2.drawTacticsCard(model2);
            });
        }
        println(model, performer.getName() + " is about to draw cards.");
        multipleChoice.promptAndDoAction(model, "Select an option:", performer);
        println(model, performer.getName() + "'s hand: ");
        performer.printHand(model.getScreenHandler());
    }

    public static void  discardIfOverLimit(Model model, Player current) {
        MultipleChoice multipleChoice = new MultipleChoice();
        for (EmpireUnitCard eu : current.getUnitCardsInHand()) {
            multipleChoice.addOption(eu.getNameAndStrength(), (m, performer) -> performer.discardCard(m, eu));
        }
        for (TacticsCard tc : current.getTacticsCardsInHand()) {
            multipleChoice.addOption(tc.getName(), (m, p) -> p.discardCard(model, tc));
        }
        if (multipleChoice.getNumberOfChoices() > MAX_HAND_SIZE) {
            model.getScreenHandler().println(current.getName() + " has too many cards in hand.");
        }
        while (multipleChoice.getNumberOfChoices() > MAX_HAND_SIZE) {
            multipleChoice.promptAndDoAction(model,"Select a card to discard:", current);
            multipleChoice.removeSelectedOption();
        }
    }

    public boolean takeDefectorTurn(Model model, DefectedPlayer defector) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption("Increase Unrest", this::increaseUnrest);
        if (defector.getHeldEvent() != null) {
            multipleChoice.addOption("Resolve Held Event", (m, p) -> {
                resolveEventThenDiscard(model, defector, defector.getHeldEvent());
                defector.removeHeldEvent();
            });
        }
        if (!defector.getRebelUnitCards().isEmpty()) {
            multipleChoice.addOption("Add 2 Units to battle", (m, p) -> {
                ((DefectedPlayer) p).addUnitsToBattle(model);
            });
        }
        multipleChoice.promptAndDoAction(model, "Select an action for " + defector.getName() + ".", defector);

        if (model.gameIsOver()) {
            return true;
        }

        // Draw Cards
        model.getScreenHandler().println(defector.getName() + " draws 2 Rebel Units:");
        for (int i = 0; i < 2; ++i) {
            try {
                defector.addCardToHand(model.drawRebelUnitCard());
            } catch (DeckIsEmptyException die) {
                model.getScreenHandler().println("Rebel Unit deck, empty, drew " + i + " card(s) instead.");
                break;
            }
        }
        defector.printHand(model.getScreenHandler());

        // Discard if over hand limit
        multipleChoice = new MultipleChoice();
        for (RebelUnitCard ru : defector.getRebelUnitCards()) {
            multipleChoice.addOption(ru.getNameAndStrength(), (m, performer) -> ((DefectedPlayer)performer).discardCard(m, ru));
        }
        if (multipleChoice.getNumberOfChoices() > MAX_HAND_SIZE) {
            println(model, defector.getName() + " has too many cards in hand.");
        }
        while (multipleChoice.getNumberOfChoices() > MAX_HAND_SIZE) {
            multipleChoice.promptAndDoAction(model,"Select a card to discard:", defector);
            multipleChoice.removeSelectedOption();
        }

        return false;
    }
}

package model.states;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.board.BoardLocation;
import model.cards.AgentUnitCard;
import model.cards.EmpireUnitCard;
import model.cards.RebelUnitCard;
import model.cards.TacticsCard;
import util.MyLists;
import view.MultipleChoice;
import view.MultipleChoiceAction;

import java.util.ArrayList;
import java.util.List;

public class PlayerActionState extends GameState {
    private static final int MAX_HAND_SIZE = 8;

    @Override
    public GameState run(Model model) {

        for (int i = 0; i < model.getPlayers().size(); ++i) {
            Player current = model.getCurrentPlayer();
            println(model, current.getName() + "'s turn. Card's in hand: ");
            current.printHand(model.getScreenHandler());
            println(model, "   Loyalty: " + current.getLoyaltyCard().getName());
            doNegativeAction(model, current);
            doPlayerAction(model, current);
            drawThreeCards(model, current);
            discardIfOverLimit(model, current);
            model.stepCurrentPlayer();
            model.drawBoard();
        }
        return new EmperorHealthDeclineState();
    }

    private void doNegativeAction(Model model, Player current) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption("Draw 2 Rebel Units", this::draw2RebelUnits);
        multipleChoice.addOption("Increase Unrest", this::increaseUnrest);
        multipleChoice.addOption("Draw Turmoil Card", this::drawTurmoilCard);
        multipleChoice.promptAndDoAction(model, "Select a negative action:", current);

    }

    public void draw2RebelUnits(Model model, Player performer) {
        println(model, performer.getName() + " draws 2 Rebel Units from the deck.");
        List<RebelUnitCard> cards = new ArrayList<>();
        cards.add(model.getRebelUnitDeck().drawOne());
        cards.add(model.getRebelUnitDeck().drawOne());

        for (int i = 0; i < 2; ++i) {
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
        checkForTriggeredBattles(model, performer);
    }

    private void checkForTriggeredBattles(Model model, Player player) {
        MultipleChoice multipleChoice = new MultipleChoice();
        for (BattleBoard bb : model.getBattles()) {
            if (bb.battleIsTriggered()) {
                multipleChoice.addOption(bb.getName(), (Model m, Player p) -> {
                    m.resolveBattle(bb);
                });
            }
        }
        while (multipleChoice.getNumberOfChoices() > 0) {
            multipleChoice.promptAndDoAction(model, "There are multiple battles which need to be resolved. Which one would you like to resolve first?", player);
            multipleChoice.removeSelectedOption();
        }
    }

    private void increaseUnrest(Model model, Player player) {
        model.increaseUnrest(1);
        println(model, "Unrest is now at " + model.getUnrest() + "/" + model.getMaxUnrest());
    }

    private void drawTurmoilCard(Model model, Player player) {
        // TODO
    }

    private void doPlayerAction(Model model, Player current) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption("Move", this::movePlayer);
        addNonMoveActions(multipleChoice, model, current);
        multipleChoice.promptAndDoAction(model, "Select an action:", current);

        if (multipleChoice.getSelectedOptionIndex() == 1) {
            MultipleChoice multipleChoice2 = new MultipleChoice();
            addNonMoveActions(multipleChoice2, model, current);
            multipleChoice2.promptAndDoAction(model, "Select an action:", current);
        } else {
            MultipleChoice multipleChoice2 = new MultipleChoice();
            multipleChoice2.addOption("Move", this::movePlayer);
            multipleChoice2.addOption("Stay in location", (_, _) -> {});
            multipleChoice2.promptAndDoAction(model, "Do you want to move?", current);
        }
    }

    private void addNonMoveActions(MultipleChoice multipleChoice, Model model, Player current) {
        if (isOnCentralia(model, current)) {
            multipleChoice.addOption("Collaborative Draw Unit Cards", this::collaborativeDrawUnitCards);
            multipleChoice.addOption("Quell Unrest", this::notYetImplemented);
            multipleChoice.addOption("Attempt Arrest", this::notYetImplemented);
        } else if (!current.getUnitCardsInHand().isEmpty()) {
            multipleChoice.addOption("Add Cards to Battle", this::addCardsToBattle);
        }
        multipleChoice.addOption("Pass", (_, _) -> {});
    }

    private void notYetImplemented(Model model, Player player) {
        println(model, "Not yet implemented!!!");
    }

    private void addCardsToBattle(Model model, Player player) {
        BattleBoard bb = (BattleBoard) player.getCurrentLocation();
        EmpireUnitCard agent = MyLists.find(player.getUnitCardsInHand(), eu -> eu instanceof AgentUnitCard);
        if (agent != null) {
            println(model, player.getName() + " has an Agent card and can play it to look at the Rebel Unit Cards on " + bb.getName() + ".");
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
            multipleChoice.promptAndDoAction(model, "Which card do you wish to add to the battle?", player);
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
            multipleChoice.addOption(dest.getName(),
                    (_, performer) -> {
                        performer.moveToLocation(dest);
                        println(model, performer.getName() + " moves to " + dest.getName() + ".");
                    });
        }
        multipleChoice.promptAndDoAction(model, "Move to which location?", player);
    }

    private boolean isOnCentralia(Model model, Player player) {
        return model.getCentralia() == player.getCurrentLocation();
    }

    private void collaborativeDrawUnitCards(Model model, Player player) {
        MultipleChoice multipleChoice = new MultipleChoice();
        for (Player p : model.getPlayers()) {
            if (p != player && isOnCentralia(model, p)) {
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
        drawThreeCards(model, performer);
        print(model, performer.getName() + "'s hand: ");
        performer.printHand(model.getScreenHandler());
        if (other != null) {
            drawThreeCards(model, other);
            print(model, other.getName() + "'s hand: ");
            other.printHand(model.getScreenHandler());
            println(model, "It's still " + performer.getName() + "'s turn.");
        }
    }

    private void drawThreeCards(Model model, Player performer) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption("3 Unit Cards", (model1, performer1) -> performer1.drawUnitCards(model1, 3));
        if (isOnCentralia(model, performer)) {
            multipleChoice.addOption("2 Unit Cards + 1 Tactics Card", (model2, performer2) -> {
                performer2.drawUnitCards(model2, 2);
                performer2.drawTacticsCard(model2);
            });
        }
        multipleChoice.promptAndDoAction(model, "Select an option:", performer);
    }

    private void discardIfOverLimit(Model model, Player current) {
        MultipleChoice multipleChoice = new MultipleChoice();
        for (EmpireUnitCard eu : current.getUnitCardsInHand()) {
            multipleChoice.addOption(eu.getNameAndStrength(), (m, performer) -> performer.discardCard(m, eu));
        }
        for (TacticsCard tc : current.getTacticsCardsInHand()) {
            multipleChoice.addOption(tc.getName(), (m, p) -> p.discardCard(model, tc));
        }
        if (multipleChoice.getNumberOfChoices() > MAX_HAND_SIZE) {
            println(model, current.getName() + " has too many cards in hand.");
        }
        while (multipleChoice.getNumberOfChoices() > MAX_HAND_SIZE) {
            multipleChoice.promptAndDoAction(model,"Select a card to discard:", current);
            multipleChoice.removeSelectedOption();
        }
    }
}

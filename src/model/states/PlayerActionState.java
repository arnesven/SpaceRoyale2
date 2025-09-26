package model.states;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.board.BoardLocation;
import model.cards.EmpireUnitCard;
import model.cards.RebelUnitCard;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public class PlayerActionState extends GameState {
    @Override
    public GameState run(Model model) {

        for (int i = 0; i < model.getPlayers().size(); ++i) {
            Player current = model.getCurrentPlayer();
            model.getScreenHandler().println(current.getName() + "'s turn. Card's in hand: ");
            current.printHand(model.getScreenHandler());
            doNegativeAction(model, current);
            doPlayerAction(model, current);

            model.stepCurrentPlayer();
            model.drawBoard();
        }
        return null;
    }

    private void doNegativeAction(Model model, Player current) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption("Draw 2 Rebel Units", this::draw2RebelUnits);
        multipleChoice.addOption("Increase Unrest", this::increaseUnrest);
        multipleChoice.addOption("Draw Turmoil Card", this::drawTurmoilCard);
        multipleChoice.promptAndDoAction(model, "Select a negative action:", current);

    }

    public void draw2RebelUnits(Model model, Player performer) {
        model.getScreenHandler().println(performer.getName() + " draws 2 Rebel Units from the deck.");
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
                            if (bb.battleIsTriggered()) {
                                model.resolveBattle(bb);
                            }
                        });
            }
            multipleChoice.promptAndDoAction(model, prompt, performer);
        }
    }

    private void increaseUnrest(Model model, Player player) {
        model.increaseUnrest(1);
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
            // TODO: If you have not already, may now move.
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
        model.getScreenHandler().println("Not yet implemented!!!");
    }

    private void addCardsToBattle(Model model, Player player) {
        MultipleChoice multipleChoice = new MultipleChoice();
        BattleBoard bb = (BattleBoard) player.getCurrentLocation();
        for (EmpireUnitCard eu : player.getUnitCardsInHand()) {
            multipleChoice.addOption(eu.getName(), (_, performer) -> {
                bb.addEmpireUnit(eu);
                performer.removeUnitCardFromHand(eu);
                if (bb.battleIsTriggered()) {
                    model.resolveBattle(bb);
                }
            });
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
                        model.getScreenHandler().println(performer.getName() + " moves to " + dest.getName() + ".");
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
        if (multipleChoice.noOfChoices() == 0) {
            drawCardsTogetherWith(model, player, null);
        } else {
            multipleChoice.promptAndDoAction(model, "Which player do you want collaboratively draw cards with?", player);
        }
    }

    private void drawCardsTogetherWith(Model model, Player performer, Player other) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption("3 Unit Cards", (model1, performer1) -> performer1.drawUnitCards(model1, 3));
        multipleChoice.addOption("2 Unit Cards + 1 Tactics Card", (model2, performer2) -> {
            performer2.drawUnitCards(model2, 2);
            performer2.drawTacticsCard(model2);
        });
        multipleChoice.promptAndDoAction(model, "Select an option:", performer);
        if (other != null) {
            multipleChoice.promptAndDoAction(model, other.getName() + ", select an option:", other);
        }
    }
}

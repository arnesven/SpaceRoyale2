package model.board;

import model.GameOverException;
import model.Model;
import model.Player;
import model.cards.RebelUnitCard;
import model.states.PlayerActionState;
import util.MyLists;
import view.MultipleChoice;

import java.util.List;

public class BattleOfCentralia extends DefendPlanetBattleBoard {
    private static final int CARDS_TO_DRAW = 10;

    public BattleOfCentralia(Model model, List<RebelUnitCard> rebelUnitCards) {
        super("Battle of Centralia", 'X');
        model.getScreenHandler().println("The Rebels are about to attack Centralia!");
        if (!rebelUnitCards.isEmpty()) {
            model.getScreenHandler().println("Adding " + rebelUnitCards.size() + " Rebel Unit cards from a previous battle.");
        }
        for (RebelUnitCard ru : rebelUnitCards) {
            addRebelCard(ru);
        }
        model.getScreenHandler().println("Adding 10 Rebel unit cards from the deck.");
        for (int i = 0; i < CARDS_TO_DRAW; ++i) {
            addRebelCard(model.drawRebelUnitCard());
        }
        for (Player p : model.getPlayers()) {
            if (p.getCurrentLocation() != model.getCentralia()) {
                MultipleChoice multipleChoice = new MultipleChoice();
                multipleChoice.addOption("Move to Centralia", (m, p2) -> p2.moveToLocation(this));
                multipleChoice.addOption("Stay in location", (_, p2) -> {
                    p2.addToEmperorInfluence(-1);
                });
                model.getScreenHandler().println("Does " + p.getName() + " rush to Centralia to defend the capital?");
                multipleChoice.promptAndDoAction(model, "(Declining incurs at a -1 Emperor Influence penalty.)", p);
            } else {
                p.moveToLocation(this);
            }
        }
        for (Player p : MyLists.filter(model.getPlayers(), p -> p.getCurrentLocation() == this)) {
            PlayerActionState.addCardsToBattle(model, p);
        }
    }

    @Override
    public boolean battleIsTriggered() {
        return false;
    }

    @Override
    protected void advanceWarCounter(Model model) {
        model.getScreenHandler().println("The War counter is reset to 0");
        model.resetWarCounter();
        movePlayersAfterBattle(model);
    }

    @Override
    protected void retreatWarCounter(Model model) {
        model.getScreenHandler().println("The Imperial forces has lost control of the Capital planet.");
        model.getScreenHandler().println("The Empire crumbles and the Rebels rejoice. All rebel players win!");
        model.setGameOver(true);
        throw new GameOverException();
    }
}

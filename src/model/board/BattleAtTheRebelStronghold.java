package model.board;

import model.DefectedPlayer;
import model.GameOverException;
import model.Model;
import model.Player;
import model.cards.units.RebelUnitCard;
import model.states.PlayerActionState;
import util.MyLists;
import view.MultipleChoice;

import java.util.List;

public class BattleAtTheRebelStronghold extends InvasionBattleBoard {
    private static final int CARDS_TO_DRAW = 10;

    public BattleAtTheRebelStronghold(Model model, List<RebelUnitCard> rebelUnitCards) {
        super("Battle at the Rebel Stronghold", 'X');
        model.getScreenHandler().println("The Empire is preparing to attack the Rebel Stronghold!");
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
        for (Player p : model.getPlayersNotDefectors()) {
            MultipleChoice multipleChoice = new MultipleChoice();
            multipleChoice.addOption("Move to Rebel Stronghold", (m, p2) -> p2.moveToLocation(this));
            multipleChoice.addOption("Stay in location", (m, p2) -> {
                p2.addToEmperorInfluence(-1);
                m.getScreenHandler().println(p2.getName() + " gets -1 Emperor Influence.");
            });
            model.getScreenHandler().println("Does " + p.getName() + " participate in the assault?");
            multipleChoice.promptAndDoAction(model, "(Declining incurs at a -1 Emperor Influence penalty.)", p);
        }
        for (Player p : model.getPlayers()) {
            if (p instanceof DefectedPlayer) {
                ((DefectedPlayer)p).addUnitsToBattle(model, this);
            } else if (p.getCurrentLocation() == this) {
                PlayerActionState.addCardsToBattle(model, p);
            }
        }
    }

    @Override
    public boolean battleIsTriggered() {
        return false;
    }

    @Override
    protected void advanceWarCounter(Model model) {
        model.getScreenHandler().println("With the vast bulk of the rebellion's forces and command wiped out,");
        model.getScreenHandler().println("the Empire easily hunts down any remaining stragglers.");
        model.getScreenHandler().println("The rebellion has been crushed! All empire players win!");
        model.setGameOver(true);
        throw new GameOverException();
    }

    @Override
    protected void retreatWarCounter(Model model) {
        model.getScreenHandler().println("The War counter is reset to 0");
        model.resetWarCounter();
        movePlayersAfterBattle(model);
    }
}

package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.board.BoardLocation;
import util.MyLists;
import view.MultipleChoice;

import java.util.List;

public class ShuttleCard extends EmpireUnitCard {
    public ShuttleCard() {
        super("Shuttle", 0, false);
    }

    @Override
    public EmpireUnitCard copy() {
        return new ShuttleCard();
    }

    public static void moveWithPlayer(Model model, Player performer, BoardLocation movedFrom, BoardLocation dest) {
        for (Player p : model.getPlayers()) {
            if (p != performer && p.getCurrentLocation() == movedFrom) {
                EmpireUnitCard shuttle = MyLists.find(p.getUnitCardsInHand(), eu -> eu instanceof ShuttleCard);
                if (shuttle != null) {
                    MultipleChoice multipleChoice = new MultipleChoice();
                    multipleChoice.addOption("Use Shuttle", (m, p2) -> {
                        p2.moveToLocation(dest);
                        p2.discardCard(m, shuttle);
                    });
                    multipleChoice.addOption("Skip", (_, _) -> {});
                    multipleChoice.promptAndDoAction(model, "Will " + p.getName() + " play a Shuttle to go with " +
                            performer.getName() + " to " + dest.getName() + "?", p);
                }
            }
        }
    }

    public void addMoveOptionsAfterBattle(Model model, MultipleChoice multipleChoice, BattleBoard outgoingBattle) {
        for (BattleBoard bb2 : model.getBattles()) {
            if (bb2 != outgoingBattle) {
                multipleChoice.addOption("To " + bb2.getName(), (m, p2) -> {
                    p2.moveToLocation(bb2);
                    p2.discardCard(m, this);
                });
            }
        }
    }
}

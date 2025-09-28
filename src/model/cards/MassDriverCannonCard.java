package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import util.MyLists;
import view.MultipleChoice;

import java.util.List;

public class MassDriverCannonCard extends TacticsCard {
    public MassDriverCannonCard() {
        super("Mass Driver Cannon");
    }

    @Override
    public boolean playedAfterReveal() {
        return true;
    }

    @Override
    public void resolve(Model model, Player player, BattleBoard battle) {
        List<RebelUnitCard> ground = MyLists.filter(battle.getRebelUnits(), ru -> ru.isSpaceUnit());
        if (ground.isEmpty()) {
            model.getScreenHandler().println("There are no Space units to discard.");
            return;
        }
        MultipleChoice multipleChoice = new MultipleChoice();
        for (RebelUnitCard ru : ground) {
            multipleChoice.addOption(ru.getName(), (m, p) -> {
                model.getScreenHandler().println("Discarding " + ru.getName() + ".");
                battle.removeUnit(ru);
                model.discardRebelCards(List.of(ru));
            });
        }
        multipleChoice.promptAndDoAction(model, "Select a ground unit to discard:", player);
    }

    @Override
    public GameCard copy() {
        return new MassDriverCannonCard();
    }
}

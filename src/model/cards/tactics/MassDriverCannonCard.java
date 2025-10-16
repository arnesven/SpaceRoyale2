package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.GameCard;
import model.cards.units.*;
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
    public boolean resolve(Model model, Player player, BattleBoard battle) {
        List<RebelUnitCard> ground = MyLists.filter(battle.getRebelUnits(), this::isDestroyableSpaceUnit);
        if (ground.isEmpty()) {
            model.getScreenHandler().println("There are no Space units to discard.");
            return false;
        }
        MultipleChoice multipleChoice = new MultipleChoice();
        for (RebelUnitCard ru : ground) {
            multipleChoice.addOption(ru.getName(), (m, p) -> {
                model.getScreenHandler().println("Discarding " + ru.getName() + ".");
                battle.removeUnit(ru);
                model.discardRebelCards(List.of(ru));
            });
        }
        for (EmpireUnitCard eu : MyLists.filter(battle.getEmpireUnits(), UnitCard::isSpaceUnit)) {
            multipleChoice.addOption(eu.getName() + " (Empire)", (m, p) -> {
                model.getScreenHandler().println("Discarding " + eu.getName() + ".");
                battle.removeUnit(eu);
                model.discardEmpireCards(List.of(eu));
            });
        }
        multipleChoice.promptAndDoAction(model, "Select a space unit to discard:", player);
        return true;
    }

    private boolean isDestroyableSpaceUnit(RebelUnitCard rebelUnitCard) {
        if (!rebelUnitCard.isSpaceUnit()) {
            return false;
        }
        if (rebelUnitCard instanceof UpgradeableRebelUnitCard) {
            return !((UpgradeableRebelUnitCard) rebelUnitCard).isUpgraded();
        }
        if (rebelUnitCard instanceof FighterAceUnitCard) {
            return false;
        }
        return true;
    }

    @Override
    public GameCard copy() {
        return new MassDriverCannonCard();
    }
}

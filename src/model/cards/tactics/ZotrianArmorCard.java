package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.GameCard;
import model.cards.units.EmpireUnitCard;
import model.cards.units.UnitCard;
import util.MyLists;
import view.MultipleChoice;

import java.util.List;

public class ZotrianArmorCard extends TacticsCard {
    public ZotrianArmorCard() {
        super("Zotrian Armor");
    }

    @Override
    public boolean playedAfterReveal() {
        return false;
    }

    @Override
    public boolean resolve(Model model, Player player, BattleBoard battle) {
        List<EmpireUnitCard> cardsToPickFrom = MyLists.filter(battle.getEmpireUnits(),
                UnitCard::isSpaceUnit);
        if (cardsToPickFrom.isEmpty()) {
            model.getScreenHandler().println("There are no suitable cards for " + this.getName() + " to recover.");
            return false;
        }

        MultipleChoice multipleChoice = new MultipleChoice();
        for (EmpireUnitCard eu : cardsToPickFrom) {
            multipleChoice.addOption(eu.getNameAndStrength(), (m, p) -> {
                model.getScreenHandler().println(p.getName() + " picks up " + eu.getName() + ".");
                battle.removeUnit(eu);
                p.addCardToHand(eu);
            });
        }
        multipleChoice.promptAndDoAction(model, "Which card do you want to pick up?", player);
        return true;
    }

    @Override
    public GameCard copy() {
        return new ZotrianArmorCard();
    }
}

package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.GameCard;
import model.cards.units.ShuttleCard;
import model.cards.units.EmpireUnitCard;
import model.cards.units.FightersCard;
import util.MyLists;
import view.MultipleChoice;

import java.util.List;

public class EvasiveManeuversCard extends TacticsCard {
    public EvasiveManeuversCard() {
        super("Evasive Maneuvers");
    }

    @Override
    public boolean playedAfterReveal() {
        return false;
    }

    @Override
    public void resolve(Model model, Player player, BattleBoard battle) {
        List<EmpireUnitCard> cardsToPickFrom = MyLists.filter(battle.getEmpireUnits(),
                eu -> eu instanceof ShuttleCard || eu instanceof FightersCard);
        if (cardsToPickFrom.isEmpty()) {
            model.getScreenHandler().println("There are no suitable cards for " + this.getName() + " to recover.");
            return;
        }

        MultipleChoice multipleChoice = new MultipleChoice();
        for (EmpireUnitCard eu : cardsToPickFrom) {
            multipleChoice.addOption(eu.getNameAndStrength(), (m, p) -> {
                model.getScreenHandler().println(p.getName() + " picks up " + eu.getName() + ".");
                battle.removeUnit(eu);
                p.addCardToHand(eu);
            });
        }
        for (int i = 0; i < 3 && multipleChoice.getNumberOfChoices() > 0; ++i) {
            multipleChoice.promptAndDoAction(model, "Which card do you want to pick up?", player);
            multipleChoice.removeSelectedOption();
        }
    }

    @Override
    public GameCard copy() {
        return new EvasiveManeuversCard();
    }
}

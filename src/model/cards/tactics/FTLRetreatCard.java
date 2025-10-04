package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.GameCard;
import model.cards.units.EmpireUnitCard;
import view.MultipleChoice;

public class FTLRetreatCard extends TacticsCard {
    public FTLRetreatCard() {
        super("FTL Retreat");
    }

    @Override
    public boolean playedAfterReveal() {
        return true;
    }

    @Override
    public void resolve(Model model, Player player, BattleBoard battle) {
        if (battle.getEmpireUnits().isEmpty()) {
            model.getScreenHandler().println(player.getName() + " moves to Centralia.");
            player.moveToLocation(model.getCentralia());
            return;
        }

        MultipleChoice multipleChoice = new MultipleChoice();
        for (EmpireUnitCard eu : battle.getEmpireUnits()) {
            multipleChoice.addOption(eu.getNameAndStrength(), (m, p) -> {
                p.addCardToHand(eu);
                battle.removeUnit(eu);
                model.getScreenHandler().println(p.getName() + " removes " + eu.getName() + " from the battle and puts it in hand.");
                model.getScreenHandler().println(p.getName() + " moves to Centralia.");
                p.moveToLocation(m.getCentralia());
            });
        }
        multipleChoice.promptAndDoAction(model, "Which card does " + player.getName() + " pick up from the battle?", player);
    }

    @Override
    public GameCard copy() {
        return new FTLRetreatCard();
    }
}

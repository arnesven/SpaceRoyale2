package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.GameCard;

public class MinefieldCodesCard extends TacticsCard {
    public MinefieldCodesCard() {
        super("Minefield Codes");
    }

    @Override
    public boolean playedAfterReveal() {
        return true;
    }

    @Override
    public boolean resolve(Model model, Player player, BattleBoard battle) {
        if (battle.battleHasMinefields()) {
            model.getScreenHandler().println("The rebel minefields have been disabled.");
            battle.disableMines();
            return true;
        }
        model.getScreenHandler().println("The card has no effect since there are no minefields in this battle.");
        return false;
    }

    @Override
    public GameCard copy() {
        return new MinefieldCodesCard();
    }
}

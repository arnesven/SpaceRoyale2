package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.GameCard;
import model.states.PlayerActionState;

public class SecurityForcesCard extends TacticsCard {
    public SecurityForcesCard() {
        super("Security Forces");
    }

    @Override
    public boolean playedAfterReveal() {
        return false;
    }

    @Override
    public boolean playAfterBattle() {
        return false;
    }

    @Override
    public boolean resolve(Model model, Player player, BattleBoard battle) {
        model.getScreenHandler().println(player.getName() + " looks at all the Empire Unit cards committed to " +
                battle.getName() + ".");
        battle.printEmpireUnits(model);
        PlayerActionState.addCardsToBattle(model, player);
        return true;
    }

    @Override
    public GameCard copy() {
        return new SecurityForcesCard();
    }
}

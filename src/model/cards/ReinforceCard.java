package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;

public class ReinforceCard extends TacticsCard {
    public ReinforceCard() {
        super("Reinforce");
    }

    @Override
    public boolean canBePlayedOutsideOfBattle() {
        return true;
    }

    @Override
    public boolean playedAfterReveal() {
        return true;
    }

    @Override
    public void resolve(Model model, Player player, BattleBoard battle) {
        if (player.getCurrentLocation() != battle) {
            model.getScreenHandler().println(player.getName() + " moves to " + battle.getName() + ".");
            player.moveToLocation(battle);
        }
        battle.reinforce(model, player);
    }

    @Override
    public GameCard copy() {
        return new ReinforceCard();
    }
}

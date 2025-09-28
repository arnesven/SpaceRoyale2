package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.board.DefendPlanetBattleBoard;
import model.board.SpaceBattleBoard;

public class AllHailTheEmperorCard extends TacticsCard {
    public AllHailTheEmperorCard() {
        super("All Hail the Emperor");
    }

    @Override
    public boolean playedAfterReveal() {
        return false;
    }

    @Override
    public void resolve(Model model, Player player, BattleBoard battle) {
        if (battle instanceof SpaceBattleBoard || battle instanceof DefendPlanetBattleBoard) {
            model.getScreenHandler().println(player.getName() + " plays " + this.getName() + " and gains 1 Empire Influence.");
            player.addToEmperorInfluence(1);
        } else {
            model.getScreenHandler().println(this.getName() + " has no effect in this type of battle.");
        }
    }

    @Override
    public GameCard copy() {
        return new AllHailTheEmperorCard();
    }
}

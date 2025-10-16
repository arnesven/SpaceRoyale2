package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.board.DefendPlanetBattleBoard;
import model.board.SpaceBattleBoard;
import model.cards.GameCard;

public class AllHailTheEmperorCard extends TacticsCard {
    public AllHailTheEmperorCard() {
        super("All Hail the Emperor");
    }

    @Override
    public boolean playedAfterReveal() {
        return false;
    }

    @Override
    public boolean resolve(Model model, Player player, BattleBoard battle) {
        if (battle instanceof SpaceBattleBoard || battle instanceof DefendPlanetBattleBoard) {
            if (battle.isEmpireVictorious()) {
                model.getScreenHandler().println(player.getName() + " plays " + this.getName() + " and gains 1 Empire Influence.");
                player.addToEmperorInfluence(1);
                return true;
            }
            model.getScreenHandler().println("Empire was not victorious, " + getName() + " has no effect.");
            return false;
        }
        model.getScreenHandler().println(this.getName() + " has no effect in this type of battle.");
        return false;
    }

    @Override
    public GameCard copy() {
        return new AllHailTheEmperorCard();
    }
}

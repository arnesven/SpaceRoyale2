package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.board.InvasionBattleBoard;
import model.cards.GameCard;

public class HumanitarianAidCard extends TacticsCard {
    public HumanitarianAidCard() {
        super("Humanitarian Aid");
    }

    @Override
    public boolean playedAfterReveal() {
        return false;
    }

    @Override
    public void resolve(Model model, Player player, BattleBoard battle) {
        if (battle instanceof InvasionBattleBoard) {
            if (battle.isEmpireVictorious()) {
                player.addToPopularInfluence(1);
                model.getScreenHandler().println(player.getName() + " plays " + this.getName() + " to gain 1 Popular Influence.");
            } else {
                model.getScreenHandler().println("Empire was not victorious, " + getName() + " has no effect.");
            }
        } else {
            model.getScreenHandler().println(getName() + " has no effect in this type of battle.");
        }
    }

    @Override
    public GameCard copy() {
        return new HumanitarianAidCard();
    }
}

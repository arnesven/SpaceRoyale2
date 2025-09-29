package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.board.InvasionBattleBoard;

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
            player.addToPopularInfluence(1); // TODO: Only if Empire victorious.
            model.getScreenHandler().println(player.getName() + " plays " + this.getName() + " to gain 1 Popular Influence.");
        } else {
            model.getScreenHandler().println(getName() + " has no effect in this type of battle.");
        }
    }

    @Override
    public GameCard copy() {
        return new HumanitarianAidCard();
    }
}

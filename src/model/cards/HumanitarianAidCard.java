package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;

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
        // TODO
    }

    @Override
    public GameCard copy() {
        return new HumanitarianAidCard();
    }
}

package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;

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
        battle.retreatPlayer(model, player);
    }

    @Override
    public GameCard copy() {
        return new FTLRetreatCard();
    }
}

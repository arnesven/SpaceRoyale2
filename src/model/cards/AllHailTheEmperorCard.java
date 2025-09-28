package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;

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
        // TODO:
    }

    @Override
    public GameCard copy() {
        return new AllHailTheEmperorCard();
    }
}

package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;

public class MassDriverCannonCard extends TacticsCard {
    public MassDriverCannonCard() {
        super("Mass Driver Cannon");
    }

    @Override
    public boolean playedAfterReveal() {
        return true;
    }

    @Override
    public void resolve(Model model, Player player, BattleBoard battle) {
        model.getScreenHandler().println("Not yet implemented!");
    }

    @Override
    public GameCard copy() {
        return new MassDriverCannonCard();
    }
}

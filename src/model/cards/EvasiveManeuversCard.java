package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;

public class EvasiveManeuversCard extends TacticsCard {
    public EvasiveManeuversCard() {
        super("Evasive Maneuvers");
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
        return new EvasiveManeuversCard();
    }
}

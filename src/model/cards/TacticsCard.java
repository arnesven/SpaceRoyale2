package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;

public abstract class TacticsCard extends GameCard {
    public TacticsCard(String name) {
        super(name);
    }

    public abstract boolean playedAfterReveal();

    public abstract void resolve(Model model, Player player, BattleBoard battle);

    public boolean canBePlayedOutsideOfBattle() {
        return false;
    }
}

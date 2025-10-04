package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.GameCard;

public abstract class TacticsCard extends GameCard {
    public TacticsCard(String name) {
        super(name);
    }

    public abstract boolean playedAfterReveal();

    public abstract void resolve(Model model, Player player, BattleBoard battle);

    public boolean canBePlayedOutsideOfBattle() {
        return false;
    }

    public boolean playAfterBattle() {
        return !playedAfterReveal();
    }
}

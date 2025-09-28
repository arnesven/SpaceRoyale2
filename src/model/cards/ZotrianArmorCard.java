package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;

public class ZotrianArmorCard extends TacticsCard {
    public ZotrianArmorCard() {
        super("Zotrian Armor");
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
        return new ZotrianArmorCard();
    }
}

package model.cards.units;

import model.cards.GameCard;

public class BattleshipCard extends EmpireUnitCard {
    public BattleshipCard() {
        super("Battleship", 6, false);
    }

    @Override
    public GameCard copy() {
        return new BattleshipCard();
    }
}

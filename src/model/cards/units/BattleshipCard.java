package model.cards.units;

import model.cards.GameCard;

public class BattleshipCard extends UpgradeableRebelUnitCard {

    public BattleshipCard() {
        super("Battleship", 7, false);
    }

    @Override
    public String getName() {
        if (isUpgraded()) {
            return "Rebel Flagship";
        }
        return super.getName();
    }

    @Override
    public GameCard copy() {
        return new BattleshipCard();
    }
}

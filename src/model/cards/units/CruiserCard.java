package model.cards.units;

import model.cards.GameCard;

public class CruiserCard extends EmpireUnitCard {
    public CruiserCard() {
        super("Cruiser", 4, false);
    }

    @Override
    public GameCard copy() {
        return new CruiserCard();
    }
}

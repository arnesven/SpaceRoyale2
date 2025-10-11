package model.cards.units;

import model.cards.GameCard;

public class CruiserCard extends RebelUnitCard {
    public CruiserCard() {
        super("Cruiser", 5, false);
    }

    @Override
    public GameCard copy() {
        return new CruiserCard();
    }
}

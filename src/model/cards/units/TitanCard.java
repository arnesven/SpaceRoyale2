package model.cards.units;

import model.cards.GameCard;

public class TitanCard extends EmpireUnitCard {
    public TitanCard() {
        super("Titan", 6, false);
    }

    @Override
    public GameCard copy() {
        return new TitanCard();
    }
}

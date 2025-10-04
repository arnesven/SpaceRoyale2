package model.cards.units;

import model.cards.GameCard;

public class TitanUnitCard extends RebelUnitCard {
    public TitanUnitCard() {
        super("Titan", 7, false);
    }

    @Override
    public GameCard copy() {
        return new TitanUnitCard();
    }
}

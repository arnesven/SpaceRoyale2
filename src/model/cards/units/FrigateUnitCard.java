package model.cards.units;

import model.cards.GameCard;

public class FrigateUnitCard extends RebelUnitCard {
    public FrigateUnitCard() {
        super("Frigate", 3, false);
    }

    @Override
    public GameCard copy() {
        return new FrigateUnitCard();
    }
}

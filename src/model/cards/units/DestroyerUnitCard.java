package model.cards.units;

import model.cards.GameCard;

public class DestroyerUnitCard extends RebelUnitCard {
    public DestroyerUnitCard() {
        super("Destroyer", 5, false);
    }

    @Override
    public GameCard copy() {
        return new DestroyerUnitCard();
    }
}

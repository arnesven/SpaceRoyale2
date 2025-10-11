package model.cards.units;

import model.cards.GameCard;

public class DestroyerCard extends EmpireUnitCard {
    public DestroyerCard() {
        super("Destroyer", 4, false);
    }

    @Override
    public GameCard copy() {
        return new DestroyerCard();
    }
}

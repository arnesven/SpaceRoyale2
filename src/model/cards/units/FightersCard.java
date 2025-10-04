package model.cards.units;

import model.cards.GameCard;

public class FightersCard extends EmpireUnitCard {
    public FightersCard() {
        super("Fighters", 2, false);
    }

    @Override
    public GameCard copy() {
        return new FightersCard();
    }
}

package model.cards.units;

import model.cards.GameCard;

public class GroundTroopsCard extends EmpireUnitCard {
    public GroundTroopsCard() {
        super("Ground Troops", 1, true);
    }

    @Override
    public GameCard copy() {
        return new GroundTroopsCard();
    }
}

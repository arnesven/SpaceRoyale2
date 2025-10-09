package model.cards.units;

import model.cards.GameCard;

public class RebelBlackCrusaderUnitCard extends SpecialRebelUnit {
    public RebelBlackCrusaderUnitCard(int strength, boolean groundUnit) {
        super("Black Crusader", strength, groundUnit);
    }

    @Override
    public GameCard copy() {
        return new RebelBlackCrusaderUnitCard(getStrength(), isGroundUnit());
    }
}

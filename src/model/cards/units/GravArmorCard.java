package model.cards.units;

import model.cards.GameCard;

public class GravArmorCard extends EmpireUnitCard {
    public GravArmorCard() {
        super("Grav Armor", 3, true);
    }

    @Override
    public GameCard copy() {
        return new GravArmorCard();
    }
}

package model.cards.units;

import model.cards.GameCard;

public class DronesUnitCard extends RebelUnitCard {
    public DronesUnitCard() {
        super("Drones", 1, false);
    }

    @Override
    public GameCard copy() {
        return new DronesUnitCard();
    }
}

package model.cards.units;

import model.cards.GameCard;

public class RebelCommandosUnitCard extends RebelUnitCard {
    public RebelCommandosUnitCard() {
        super("Commandos", 6, true);
    }

    @Override
    public GameCard copy() {
        return new RebelCommandosUnitCard();
    }
}

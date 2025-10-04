package model.cards.units;

import model.cards.GameCard;

public class RebelSoldiersCard extends RebelUnitCard {
    public RebelSoldiersCard() {
        super("Rebel Soldiers", 2, true);
    }

    @Override
    public GameCard copy() {
        return new RebelSoldiersCard();
    }
}

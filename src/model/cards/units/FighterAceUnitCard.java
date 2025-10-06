package model.cards.units;

import model.cards.GameCard;

public class FighterAceUnitCard extends RebelUnitCard {
    public FighterAceUnitCard() {
        super("Fighter Ace", 6, false);
    }

    @Override
    public GameCard copy() {
        return new FighterAceUnitCard();
    }
}

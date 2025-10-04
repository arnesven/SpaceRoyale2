package model.cards.units;

import model.cards.GameCard;

public class StarWarriorUnitCard extends RebelUnitCard {
    public StarWarriorUnitCard() {
        super("Star Warrior", 4, true);
    }

    @Override
    public GameCard copy() {
        return new StarWarriorUnitCard();
    }
}

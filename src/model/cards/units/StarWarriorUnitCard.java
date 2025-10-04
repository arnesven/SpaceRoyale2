package model.cards.units;

import model.cards.GameCard;

public class StarWarriorUnitCard extends UpgradeableRebelUnitCard {

    public StarWarriorUnitCard() {
        super("Star Warrior", 4, true);
    }

    @Override
    public String getName() {
        if (isUpgraded()) {
            return "Champion of Light";
        }
        return super.getName();
    }

    @Override
    public GameCard copy() {
        return new StarWarriorUnitCard();
    }

}

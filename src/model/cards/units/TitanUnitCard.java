package model.cards.units;

import model.cards.GameCard;

public class TitanUnitCard extends UpgradeableRebelUnitCard {

    public TitanUnitCard() {
        super("Titan", 7, false);
    }

    @Override
    public String getName() {
        if (isUpgraded()) {
            return "Super " + super.getName();
        }
        return super.getName();
    }

    @Override
    public GameCard copy() {
        return new TitanUnitCard();
    }
}

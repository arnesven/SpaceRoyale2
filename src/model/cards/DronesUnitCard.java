package model.cards;

public class DronesUnitCard extends RebelUnitCard {
    public DronesUnitCard() {
        super("Drones", 1, false);
    }

    @Override
    public GameCard copy() {
        return new DronesUnitCard();
    }
}

package model.cards;

public class FrigateUnitCard extends RebelUnitCard {
    public FrigateUnitCard() {
        super("Frigate", 3, false);
    }

    @Override
    public GameCard copy() {
        return new FrigateUnitCard();
    }
}

package model.cards;

public class TitanUnitCard extends RebelUnitCard {
    public TitanUnitCard() {
        super("Titan", 7, false);
    }

    @Override
    public GameCard copy() {
        return new TitanUnitCard();
    }
}

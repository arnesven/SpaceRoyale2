package model.cards;

public class DestroyerUnitCard extends RebelUnitCard {
    public DestroyerUnitCard() {
        super("Destroyer", 5, false);
    }

    @Override
    public GameCard copy() {
        return new DestroyerUnitCard();
    }
}

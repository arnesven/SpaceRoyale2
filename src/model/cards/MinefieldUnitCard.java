package model.cards;

public class MinefieldUnitCard extends RebelUnitCard {
    public MinefieldUnitCard() {
        super("Minefield", 0, false);
    }

    @Override
    public RebelUnitCard copy() {
        return new MinefieldUnitCard();
    }
}

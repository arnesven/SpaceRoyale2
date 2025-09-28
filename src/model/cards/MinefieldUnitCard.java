package model.cards;

public class MinefieldUnitCard extends RebelUnitCard {
    public MinefieldUnitCard() {
        super("Minefield", 0, false);
    }

    @Override
    public boolean isSpaceUnit() {
        return false;
    }

    @Override
    public boolean isGroundUnit() {
        return false;
    }

    @Override
    public RebelUnitCard copy() {
        return new MinefieldUnitCard();
    }
}

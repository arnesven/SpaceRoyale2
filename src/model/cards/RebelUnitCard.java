package model.cards;

public class RebelUnitCard extends UnitCard {
    public RebelUnitCard(String name, int strength, boolean isGround) {
        super(name, strength, isGround);
    }

    @Override
    public RebelUnitCard copy() {
        return new RebelUnitCard(getName(), getStrength(), isGroundUnit());
    }
}

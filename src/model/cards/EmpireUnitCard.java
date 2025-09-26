package model.cards;

public class EmpireUnitCard extends UnitCard {
    public EmpireUnitCard(String name, int strength, boolean isGround) {
        super(name, strength, isGround);
    }

    @Override
    public EmpireUnitCard copy() {
        return new EmpireUnitCard(getName(), getStrength(), isGroundUnit());
    }
}

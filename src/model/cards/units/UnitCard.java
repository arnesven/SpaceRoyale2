package model.cards.units;

import model.cards.GameCard;

public abstract class UnitCard extends GameCard {
    private final int strength;
    private final boolean isGround;

    public UnitCard(String name, int strength, boolean isGround) {
        super(name);
        this.strength = strength;
        this.isGround = isGround;
    }

    public int getStrength() {
        return strength;
    }

    public boolean isGroundUnit() {
        return isGround;
    }

    public boolean isSpaceUnit() {
        return !isGroundUnit();
    }

    public String getNameAndStrength() {
        return getName() + " (" + getStrength() + ")";
    }
}

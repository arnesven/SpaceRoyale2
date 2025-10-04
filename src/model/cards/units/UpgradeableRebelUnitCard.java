package model.cards.units;

public abstract class UpgradeableRebelUnitCard extends RebelUnitCard {

    private boolean isUpgraded = false;

    public UpgradeableRebelUnitCard(String name, int strength, boolean isGround) {
        super(name, strength, isGround);
    }

    @Override
    public int getStrength() {
        if (isUpgraded()) {
            return getStrength() * 2;
        }
        return super.getStrength();
    }

    public void upgrade() {
        isUpgraded = true;
    }

    public boolean isUpgraded() {
        return isUpgraded;
    }

    public void reset() {
        isUpgraded = false;
    }
}

package model.cards.units;

import model.cards.GameCard;

public class EmpireBlackCrusaderUnitCard extends SpecialEmpireUnitCard {
    private final boolean isRebel;

    public EmpireBlackCrusaderUnitCard(boolean isGround, boolean isRebel) {
        super("Black Crusader", 8, isGround);
        this.isRebel = isRebel;
    }

    @Override
    public String getName() {
        return (isRebel ? "Rebel ":"Empire ") + super.getName() + " " + (isGroundUnit()?"(Ground)":"(Space)");
    }

    public boolean isRebel() {
        return isRebel;
    }

    @Override
    public GameCard copy() {
        return new EmpireBlackCrusaderUnitCard(isGroundUnit(), isRebel);
    }

    public RebelUnitCard makeRebelCard() {
        return new RebelBlackCrusaderUnitCard(getStrength(), isGroundUnit());
    }
}

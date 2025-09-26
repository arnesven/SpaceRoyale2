package model.cards;

public abstract class AlignmentCard extends GameCard {
    private final int rebelBonus;

    public AlignmentCard(String name, int bonus) {
        super(name);
        this.rebelBonus = bonus;
    }

    public int rebelBattleBonus() {
        return rebelBonus;
    }
}

package model.cards;

public class TacticsCard extends GameCard {
    public TacticsCard(String name) {
        super(name);
    }

    @Override
    public GameCard copy() {
        return new TacticsCard(getName());
    }
}

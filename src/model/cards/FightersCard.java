package model.cards;

public class FightersCard extends EmpireUnitCard {
    public FightersCard() {
        super("Fighters", 2, false);
    }

    @Override
    public GameCard copy() {
        return new FightersCard();
    }
}

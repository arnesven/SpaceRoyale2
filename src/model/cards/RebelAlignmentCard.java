package model.cards;

public class RebelAlignmentCard extends AlignmentCard {
    public RebelAlignmentCard() {
        super("Rebel", 2);
    }

    @Override
    public GameCard copy() {
        return new RebelAlignmentCard();
    }
}

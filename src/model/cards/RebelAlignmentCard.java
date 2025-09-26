package model.cards;

public class RebelAlignmentCard extends AlignmentCard {
    public RebelAlignmentCard() {
        super("Rebel");
    }

    @Override
    public GameCard copy() {
        return new RebelAlignmentCard();
    }
}

package model.cards;

public class EmpireAlignmentCard extends AlignmentCard {
    public EmpireAlignmentCard() {
        super("Empire");
    }

    @Override
    public GameCard copy() {
        return new EmpireAlignmentCard();
    }
}

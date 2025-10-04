package model.cards.alignment;

import model.cards.GameCard;

public class EmpireAlignmentCard extends AlignmentCard {
    public EmpireAlignmentCard() {
        super("Empire", 0);
    }

    @Override
    public GameCard copy() {
        return new EmpireAlignmentCard();
    }
}

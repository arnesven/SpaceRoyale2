package model.cards.alignment;

import model.cards.GameCard;

public class NoAlignmentCard extends AlignmentCard {
    public NoAlignmentCard() {
        super("None (Defector)", 0);
    }

    @Override
    public GameCard copy() {
        return new NoAlignmentCard();
    }
}

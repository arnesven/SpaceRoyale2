package model.cards.alignment;

import model.cards.GameCard;

public class RebelAlignmentCard extends AlignmentCard {
    public RebelAlignmentCard() {
        super("Rebel", 3);
    }

    @Override
    public GameCard copy() {
        return new RebelAlignmentCard();
    }
}

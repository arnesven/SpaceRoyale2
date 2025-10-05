package model.cards.events;

import model.cards.GameCard;
import model.cards.tactics.EvasiveManeuversCard;
import model.cards.tactics.FTLRetreatCard;

public class TachyonDrivesEventCard extends TacticsImprovementEventCard {
    private final FTLRetreatCard card1;
    private final EvasiveManeuversCard card2;

    public TachyonDrivesEventCard() {
        super("Tachyon Drives", "Keep this card in play. After a player plays a 'FTL Retreat' or 'Evasive Maneuvers' " +
                "Tactics card, roll a die. On a result of 6 or higher, the card is returned to the player's hand. " +
                "Otherwise it is discarded.");
        card1 = new FTLRetreatCard();
        card2 = new EvasiveManeuversCard();
    }

    @Override
    public String getAffectedTactics() {
        return card1.getName() + " or " + card2.getName();
    }

    @Override
    public GameCard copy() {
        return new TachyonDrivesEventCard();
    }
}

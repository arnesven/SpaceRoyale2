package model.cards.events;

import model.cards.GameCard;
import model.cards.tactics.HumanitarianAidCard;
import model.cards.tactics.ReinforceCard;

public class AdvancedLogisticsEventCard extends TacticsImprovementEventCard {
    private final ReinforceCard card1;
    private final HumanitarianAidCard card2;

    public AdvancedLogisticsEventCard() {
        super("Advanced Logistics", "Keep this card in play. After a player plays a 'Reinforce' " +
                " or 'Humanitarian Aid' Tactics card, roll a die. On a result of 6 or higher, the card is returned to the player's hand. " +
                "Otherwise it is discarded.");
        card1 = new ReinforceCard();
        card2 = new HumanitarianAidCard();
    }

    @Override
    public String getAffectedTactics() {
        return card1.getName() + " or " + card2.getName();
    }

    @Override
    public GameCard copy() {
        return new AdvancedLogisticsEventCard();
    }
}

package model.cards.events;

import model.cards.GameCard;
import model.cards.tactics.MinefieldCodesCard;

public class QuantumComputationEventCard extends TacticsImprovementEventCard {
    private final MinefieldCodesCard card;

    public QuantumComputationEventCard() {
        super("Quantum Computation", "Keep this card in play. After a player plays a 'Minefield Codes' " +
                "Tactics card, roll a die. On a result of 6 or higher, the card is returned to the player's hand. " +
                "Otherwise it is discarded.");
        card = new MinefieldCodesCard();
    }

    @Override
    public String getAffectedTactics() {
        return card.getName();
    }

    @Override
    public GameCard copy() {
        return new QuantumComputationEventCard();
    }
}

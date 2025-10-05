package model.cards.events;

import model.cards.GameCard;
import model.cards.tactics.BombardmentCard;

public class NeutrinoBombsEventCard extends TacticsImprovementEventCard {
    private final BombardmentCard card;

    public NeutrinoBombsEventCard() {
        super("Neutrino Bombs", "Keep this card in play. After a player plays a 'Bombardment' " +
                "Tactics card, roll a die. On a result of 6 or higher, the card is returned to the player's hand. " +
                "Otherwise it is discarded.");
        this.card = new BombardmentCard();
    }

    @Override
    public GameCard copy() {
        return new NeutrinoBombsEventCard();
    }

    @Override
    public String getAffectedTactics() {
        return card.getName();
    }
}

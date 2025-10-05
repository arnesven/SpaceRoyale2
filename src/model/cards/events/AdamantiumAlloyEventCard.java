package model.cards.events;

import model.cards.GameCard;
import model.cards.tactics.MassDriverCannonCard;
import model.cards.tactics.ZotrianArmorCard;

public class AdamantiumAlloyEventCard extends TacticsImprovementEventCard {
    private final MassDriverCannonCard card1;
    private final ZotrianArmorCard card2;

    public AdamantiumAlloyEventCard() {
        super("Adamantium Alloy", "Keep this card in play. After a player plays a 'Mass Driver Cannon' or 'Zotrian Armor' " +
                "Tactics card, roll a die. On a result of 6 or higher, the card is returned to the player's hand. " +
                "Otherwise it is discarded.");
        card1 = new MassDriverCannonCard();
        card2 = new ZotrianArmorCard();
    }

    @Override
    public String getAffectedTactics() {
        return card1.getName() + " or " + card2.getName();
    }

    @Override
    public GameCard copy() {
        return new AdamantiumAlloyEventCard();
    }
}

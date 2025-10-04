package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;

public class SuperTitanEventCard extends EventCard {
    public SuperTitanEventCard() {
        super("Super Titan", false, "Keep this card in play until the next battle where a " +
                "Titan Rebel Unit card appears. That card cannot be discarded by Tactics cards and has " +
                "its strength doubled. At the end of that battle, discard this card.");
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getEventCardsInPlay().add(this);
        model.getScreenHandler().println(getName() + " put into play. Next Titan will be upgraded.");
    }

    @Override
    public boolean staysInPlay() {
        return true;
    }

    @Override
    public GameCard copy() {
        return new SuperTitanEventCard();
    }
}

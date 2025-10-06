package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;

public class OppresiveBureaucracyEventCard extends EventCard {
    public OppresiveBureaucracyEventCard() {
        super("Oppressive Bureaucracy", false,
                "Increase Unrest by one, and keep this card in play. " +
                "Add 1 to the die roll in the Unrest step for this and each " +
                        "subsequent turn.");
    }

    @Override
    public boolean staysInPlay() {
        return true;
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println("Unrest increases by 1.");
        model.increaseUnrest(1);
        model.getScreenHandler().println(getName() + " is put into play. " +
                "The die roll during the Unrest step will now receive a +1 modifier.");
    }

    @Override
    public GameCard copy() {
        return new OppresiveBureaucracyEventCard();
    }
}

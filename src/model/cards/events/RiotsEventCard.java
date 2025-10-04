package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;

public class RiotsEventCard extends EventCard {
    public RiotsEventCard() {
        super("Riots", false, "Advance the Unrest counter twice.");
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println("Unrest increases by 2.");
        model.increaseUnrest(2);
    }

    @Override
    public GameCard copy() {
        return new RiotsEventCard();
    }
}

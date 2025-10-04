package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;

public class ViolentProtestsEventCard extends EventCard {
    public ViolentProtestsEventCard() {
        super("Violent Protests", false, "Advance the Unrest counter one step.");
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println("Unrest increases by one.");
        model.increaseUnrest(1);

    }

    @Override
    public GameCard copy() {
        return new ViolentProtestsEventCard();
    }
}

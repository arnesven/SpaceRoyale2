package model.cards.special;

import model.Model;
import model.cards.GameCard;

public abstract class SpecialEventCard extends GameCard {
    public SpecialEventCard(String name) {
        super(name);
    }

    public abstract void resolve(Model model);

    public void replace(Model model) {
        model.getScreenHandler().println("Returning " + getName() + " to the special events.");
        model.getSpecialEvents().returnCard(this);
        model.getSpecialEvents().placeRandomSpecialEvents(model, 1);
    }
}

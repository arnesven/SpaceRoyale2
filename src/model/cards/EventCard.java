package model.cards;

import model.Model;
import model.Player;

public class EventCard extends GameCard {
    public EventCard(String name) {
        super(name);
    }

    @Override
    public GameCard copy() {
        return new EventCard(getName());
    }

    public void resolve(Model model, Player player) {
        // TODO
    }
}

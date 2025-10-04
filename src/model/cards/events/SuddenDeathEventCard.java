package model.cards.events;

import model.GameOverException;
import model.Model;
import model.Player;
import model.cards.GameCard;

public class SuddenDeathEventCard extends EventCard {
    public SuddenDeathEventCard() {
        super("Sudden Death", true, "If the Emperor Health counter is less than three spaces away from its final space, the game immediately ends in a 'Emperor Death' ending.");
    }

    @Override
    public void resolve(Model model, Player player) {
        if (model.getEmperorHealth() >= model.getEmperorMaxHealth() - 2) {
            model.setEmperorHealth(model.getEmperorMaxHealth());
            throw new GameOverException();
        }
        model.getScreenHandler().println("Emperor health not within threshold, event has no effect.");
    }

    @Override
    public GameCard copy() {
        return new SuddenDeathEventCard();
    }
}

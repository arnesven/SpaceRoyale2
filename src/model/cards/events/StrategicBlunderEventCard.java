package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;

public class StrategicBlunderEventCard extends EventCard {
    public StrategicBlunderEventCard() {
        super("Strategic Blunder", false, "Move the War counter one step closer to the 'Battle of Centralia' space.");
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println("War counter advances one step.");
        model.advanceWarCounter();
    }

    @Override
    public GameCard copy() {
        return new StrategicBlunderEventCard();
    }
}

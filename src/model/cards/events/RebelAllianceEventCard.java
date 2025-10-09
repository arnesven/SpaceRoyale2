package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;

public class RebelAllianceEventCard extends EventCard {
    public RebelAllianceEventCard() {
        super("Rebel Alliance", false,
                "Keep this card in play. Each time a new Battle is set up, " +
                        "add one additional Rebel Unit to it.");
    }

    @Override
    public boolean staysInPlay() {
        return true;
    }

    @Override
    public void resolve(Model model, Player player) {
        model.incrementInitialRebelUnitRate();
        model.getScreenHandler().println("Battles will now start with " + model.getInitialRebelUnitRate() + " Rebel Unit cards.");
    }

    @Override
    public GameCard copy() {
        return new RebelAllianceEventCard();
    }
}

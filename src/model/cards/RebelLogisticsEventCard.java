package model.cards;

import model.Model;
import model.Player;

public class RebelLogisticsEventCard extends EventCard {
    public RebelLogisticsEventCard() {
        super("Rebel Logistics", false,
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
        return new RebelLogisticsEventCard();
    }
}

package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.states.EmperorHealthDeclineState;

public class UnrelentingIllnessEventCard extends EventCard {
    public UnrelentingIllnessEventCard() {
        super("Unrelenting Illness", true, "Immediately make an Emperor Health roll.");
    }

    @Override
    public void resolve(Model model, Player player) {
        new EmperorHealthDeclineState().run(model);
    }

    @Override
    public GameCard copy() {
        return new UnrelentingIllnessEventCard();
    }
}

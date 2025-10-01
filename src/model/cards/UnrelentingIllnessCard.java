package model.cards;

import model.Model;
import model.Player;
import model.states.EmperorHealthDeclineState;

public class UnrelentingIllnessCard extends EventCard {
    public UnrelentingIllnessCard() {
        super("Unrelenting Illness", true, "Immediately make an Emperor Health roll.");
    }

    @Override
    public void resolve(Model model, Player player) {
        new EmperorHealthDeclineState().run(model);
    }

    @Override
    public GameCard copy() {
        return new UnrelentingIllnessCard();
    }
}

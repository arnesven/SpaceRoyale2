package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;
import util.MyRandom;

public class MiraculousRecoveryEventCard extends EventCard {
    public MiraculousRecoveryEventCard() {
        super("Miraculous Recovery", true,
                "Roll the die to determine how many spaces the Health counter retreats on the Emperor Health Track.");
    }

    @Override
    public void resolve(Model model, Player player) {
        if (model.getEmperorHealth() == 0) {
            model.getScreenHandler().println("Emperor health at 0, event has no effect.");
            return;
        }
        int dieRoll = MyRandom.rollD10();
        model.getScreenHandler().println("The die is rolled, it's a " + dieRoll + ".");
        model.getScreenHandler().print("The Health counter retreats ");
        int retreat = dieRoll <= 6 ? 1 : dieRoll <= 8 ? 2 : 3;
        model.getScreenHandler().println(retreat + " spaces. Now on " + model.getEmperorHealth() + ".");
        model.advanceHealthCounter(-retreat);
    }

    @Override
    public GameCard copy() {
        return new MiraculousRecoveryEventCard();
    }
}

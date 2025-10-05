package model.cards.events;

import model.Model;
import model.Player;
import model.cards.tactics.TacticsCard;
import util.MyRandom;

public abstract class TacticsImprovementEventCard extends EventCard {

    public TacticsImprovementEventCard(String name, String description) {
        super(name, false, description);
    }

    public static boolean doesKeepCard(Model model, Player player, TacticsCard tc) {
        int dieRoll = MyRandom.rollD10();
        model.getScreenHandler().println(player.getName() + " rolls a die, it's a " + dieRoll + ".");
        if (dieRoll >= 6) {
            model.getScreenHandler().println(player.getName() + " keeps " + tc.getName() + " in hand.");
            return true;
        }
        model.getScreenHandler().println(player.getName() + " discards " + tc.getName() + ".");
        return false;
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println("Neutrino Bombs put into play. " + getAffectedTactics() +
                " tactics cards will now be retained on die rolls of 6 or higher.");
    }

    public abstract String getAffectedTactics();

    @Override
    public boolean staysInPlay() {
        return true;
    }
}

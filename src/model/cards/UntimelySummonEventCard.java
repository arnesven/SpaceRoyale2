package model.cards;

import model.Model;
import model.Player;
import util.MyLists;
import view.MultipleChoice;

public class UntimelySummonEventCard extends EventCard {
    public UntimelySummonEventCard() {
        super("Untimely Summon", false, "Each player not on Centralia must immediately return there or lose 1 Emperor Influence.");
    }

    @Override
    public void resolve(Model model, Player player) {
        if (MyLists.all(model.getPlayers(), p -> p.getCurrentLocation() == model.getCentralia())) {
            model.getScreenHandler().println("All players are on Centralia, event has no effect.");
            return;
        }
        model.getScreenHandler().println("Each player must return to Centralia or lose 1 Emperor Influence.");
        for (Player p : MyLists.filter(model.getPlayersStartingFrom(player),
                p -> p.getCurrentLocation() != model.getCentralia())) {
            MultipleChoice multipleChoice = new MultipleChoice();
            multipleChoice.addOption("Return to Centralia", (m, p2) -> {
                p2.moveToLocation(m.getCentralia());
                m.getScreenHandler().println(p2.getName() + " moves to Centralia.");
            });
            multipleChoice.addOption("Stay on " + p.getCurrentLocation().getName() + " (lose 1 EI)", (m, p2) -> {
                m.getScreenHandler().println(p2.getName() + " opts to stay on " + p2.getCurrentLocation().getName() + ", loses 1 Emperor Influence.");
                p2.addToEmperorInfluence(-1);
            });
            multipleChoice.promptAndDoAction(model, "Make a decision for " + p.getName() + ".", p);
        }
    }

    @Override
    public GameCard copy() {
        return new UntimelySummonEventCard();
    }
}

package model.cards.events;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.GameCard;
import util.MyLists;
import view.MultipleChoice;

public class PrisonBreakEventCard extends EventCard {
    public PrisonBreakEventCard() {
        super("Prison Break", true,
                "Any player on the Prison planet may immediately " +
                        "move to Centralia or a remote location.");
    }

    @Override
    public void resolve(Model model, Player player) {
        for (Player p : MyLists.filter(model.getPlayersStartingFrom(player),
                p -> p.getCurrentLocation() == model.getPrisonPlanet())) {
            MultipleChoice multipleChoice = new MultipleChoice();
            multipleChoice.addOption(model.getCentralia().getName(), (m, p2) -> {
                p2.moveToLocation(m.getCentralia());
                m.getScreenHandler().println(p2.getName() + " moves to Centralia.");
            });
            for (BattleBoard bb : model.getBattles()) {
                multipleChoice.addOption(bb.getName(), (m, p2) -> {
                    p2.moveToLocation(bb);
                    m.getScreenHandler().println(p2.getName() + " moves to " + bb.getName() + ".");
                });
            }
            multipleChoice.addOption("Don't move", (m, p2) -> {
                m.getScreenHandler().println(p2.getName() + " stays on the Prison planet.");
            });
            multipleChoice.promptAndDoAction(model, "Select an action for " + p.getName() + ".", p);
        }
    }

    @Override
    public GameCard copy() {
        return new PrisonBreakEventCard();
    }
}

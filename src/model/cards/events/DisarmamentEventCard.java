package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.tactics.BombardmentCard;
import model.cards.tactics.TacticsCard;
import util.MyLists;
import view.MultipleChoice;

import java.util.List;

public class DisarmamentEventCard extends EventCard {
    public DisarmamentEventCard() {
        super("Disarmament", false,
                "Any player may immediately discard one or more Bombardment Tactics " +
                        "cards to receive 1 Popular Influence for each card discarded.");
    }

    @Override
    public void resolve(Model model, Player player) {
        List<Player> playersWithBombs = MyLists.filter(model.getPlayersStartingFrom(player),
                p -> MyLists.any(p.getTacticsCardsInHand(), tc -> tc instanceof BombardmentCard));
        for (Player p : playersWithBombs) {
            boolean[] done = new boolean[]{false};
            while (!done[0]) {
                MultipleChoice multipleChoice = new MultipleChoice();
                TacticsCard bombardment = MyLists.find(p.getTacticsCardsInHand(), tc -> tc instanceof BombardmentCard);
                multipleChoice.addOption("Discard " + bombardment.getName() + " to get 1 Popular Influence", (m, p2) -> {
                    p2.discardCard(model, bombardment);
                    p2.addToPopularInfluence(1);
                    m.getScreenHandler().println(p2.getName() + " discarded a " + bombardment.getName() +
                            " to get 1 Popular Influence.");
                });
                multipleChoice.addOption("Skip", (m, p2) -> {
                    done[0] = true;
                });
                multipleChoice.promptAndDoAction(model, "Make a decision for " + p.getName(), p);
            }
        }
    }

    @Override
    public GameCard copy() {
        return new DisarmamentEventCard();
    }
}

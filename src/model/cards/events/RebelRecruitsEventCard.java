package model.cards.events;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.DeckIsEmptyException;
import model.cards.GameCard;

public class RebelRecruitsEventCard extends EventCard {
    public RebelRecruitsEventCard() {
        super("Rebel Recruits", false,
                "For each battle, draw a Rebel Unit card and add " +
                        "it to that battle without looking at it.");
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println("A Rebel Unit card added to each battle.");
        for (BattleBoard bb : model.getBattles()) {
            try {
                bb.addRebelCard(model.drawRebelUnitCard());
            } catch (DeckIsEmptyException die) {
                model.getScreenHandler().println("Rebel Unit deck is empty. No card added to " + bb.getName() + ".");
            }
        }
    }

    @Override
    public GameCard copy() {
        return new RebelRecruitsEventCard();
    }
}

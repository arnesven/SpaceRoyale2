package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.alignment.AlignmentCard;

public class ImperialSpyEventCard extends EventCard {
    public ImperialSpyEventCard() {
        super("Imperial Spy", false,
                "The current player may look at the top card of the Battle Chance deck.");
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println(player.getName() + " peeks at the top card of the Battle Chance deck.");
        AlignmentCard card = model.peekAtBattleChanceCard();
        model.getScreenHandler().println("It's " + card.getName() + ".");
    }

    @Override
    public GameCard copy() {
        return new ImperialSpyEventCard();
    }
}

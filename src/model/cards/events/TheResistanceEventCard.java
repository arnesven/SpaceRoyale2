package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.alignment.RebelAlignmentCard;
import util.MyLists;

import java.util.List;

public class TheResistanceEventCard extends EventCard {
    public TheResistanceEventCard() {
        super("The Resistance", true,
                "Increase Unrest by one. All players must close their eyes. " +
                "Then, all players with Rebel loyalty can open their eyes to identify" +
                        " each other. Then everybody closes their eyes again.");
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println("Unreast increases by 1.");
        model.increaseUnrest(1);
        model.getScreenHandler().println("Everybody closes their eyes.");

        List<Player> traitors = MyLists.filter(model.getPlayers(),
                p -> p.getLoyaltyCard() instanceof RebelAlignmentCard);
        if (traitors.size() == 1) {
            model.getScreenHandler().println(traitors.getFirst().getName() + " opens their eyes and realizes they're alone.");
            model.getScreenHandler().println(traitors.getFirst().getName() + " closes their eyes again.");
        } else if (traitors.size() == 2) {
            model.getScreenHandler().println(MyLists.commaAndJoin(traitors, Player::getName) +
                    " open their eyes and smile at each other. Then they close their eyes again.");
        }
        model.getScreenHandler().println("Everybody opens their eyes again.");
    }

    @Override
    public GameCard copy() {
        return new TheResistanceEventCard();
    }
}

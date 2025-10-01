package model.cards;

import model.Model;
import model.Player;
import util.MyLists;

public class BoilingPointEventCard extends EventCard {
    public BoilingPointEventCard() {
        super("The Boiling Point", true,
                "Count the number of Unit cards in the hands of players on Centralia. " +
                        "If it is more than twice the number of players in the game, set the Unrest " +
                        "counter to the third space from the 'Revolution' space.");
    }

    @Override
    public void resolve(Model model, Player player) {
        int unitsOnCentralia = MyLists.intAccumulate(MyLists.filter(model.getPlayers(),
                        p -> p.getCurrentLocation() == model.getCentralia()),
                        p -> player.getUnitCardsInHand().size());
        model.getScreenHandler().println("Number of units on centralia: " + unitsOnCentralia);
        if (unitsOnCentralia > model.getPlayers().size() * 2) {
            model.getScreenHandler().println("More units than 2 x number of players. Setting Unrest to 7.");
            model.setUnrest(7);
        } else {
            model.getScreenHandler().println("Less than 2 x number of players.");
        }
    }

    @Override
    public GameCard copy() {
        return new BoilingPointEventCard();
    }
}

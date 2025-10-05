package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;
import util.MyLists;
import util.MyRandom;

public class WitchHuntEventCard extends EventCard {
    public WitchHuntEventCard() {
        super("Witch Hunt", false,
                "Each player with the least Popular Influence must roll a die. " +
                        "On a result of 1-4, move to the Prison Planet.");
    }

    @Override
    public void resolve(Model model, Player player) {
        int leastPopularInfluence = MyLists.minimum(model.getPlayers(), Player::getPopularInfluence);
        for (Player p : MyLists.filter(model.getPlayersStartingFrom(player),
                p -> p.getPopularInfluence() == leastPopularInfluence &&
                p.getCurrentLocation() != model.getPrisonPlanet())) {
            int dieRoll = MyRandom.rollD10();
            model.getScreenHandler().print(p.getName() + " rolls a die, it is " + dieRoll + ", ");
            if (dieRoll <= 4) {
                model.getScreenHandler().println("moves to Prison planet!");
                p.moveToLocation(model.getPrisonPlanet());
            } else {
                model.getScreenHandler().println("no effect.");
            }
        }
    }

    @Override
    public GameCard copy() {
        return new WitchHuntEventCard();
    }
}

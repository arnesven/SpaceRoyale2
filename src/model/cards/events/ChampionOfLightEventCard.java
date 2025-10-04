package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;

public class ChampionOfLightEventCard extends EventCard {
    public ChampionOfLightEventCard() {
        super("Champion of Light", false, "Keep this card in play until the next Defend Planet or Invasion battle where a Star Warrior " +
                "Rebel Unit card appears. That card cannot be discarded by Tactics cards and has its " +
                "strength doubled. At the end of that battle, discard this card.");
    }

    @Override
    public boolean staysInPlay() {
        return true;
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println(getName() + " put into play. Next Star Warrior will be upgraded (if Invasion or Defend Planet battle).");
    }

    @Override
    public GameCard copy() {
        return new ChampionOfLightEventCard();
    }
}

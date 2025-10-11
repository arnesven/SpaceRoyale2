package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;

public class RebelFlagshipCard extends EventCard {
    public RebelFlagshipCard() {
        super("Rebel Flagship", false, "Keep this card in play until the next battle where a " +
                "Battleship Unit card appears. That card cannot be discarded by Tactics cards and has " +
                "its strength doubled. At the end of that battle, discard this card.");
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println(getName() + " put into play. Next Battleship will be upgraded.");
    }

    @Override
    public boolean staysInPlay() {
        return true;
    }

    @Override
    public GameCard copy() {
        return new RebelFlagshipCard();
    }
}

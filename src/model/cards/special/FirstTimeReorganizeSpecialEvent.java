package model.cards.special;

import model.Model;
import model.cards.GameCard;

public class FirstTimeReorganizeSpecialEvent extends ReorganizeSpecialEvent {
    @Override
    public void replace(Model model) {
        model.getSpecialEvents().removeCard(this);
        int numPlayers = model.getPlayers().size();
        model.getSpecialEvents().placeRandomSpecialEvents(model,
                numPlayers == 5 ? 1 : (numPlayers == 8 ? 3 : 2));
    }

    @Override
    public GameCard copy() {
        return new FirstTimeReorganizeSpecialEvent();
    }
}

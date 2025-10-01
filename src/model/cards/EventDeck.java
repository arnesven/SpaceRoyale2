package model.cards;

import java.util.List;

public class EventDeck extends Deck<EventCard> {

    public EventDeck() {
        addCard(new RebelLogistics());
        addCard(new BandingTogetherEventCard());
        addCard(new RebelRecruitsEventCard());
        shuffle();
    }

    public EventDeck(List<EventCard> cards) {
        for (EventCard c : cards) {
            addCard(c);
        }
    }
}

package model.cards;

import java.util.List;

public class EventDeck extends Deck<EventCard> {

    public EventDeck() {
        addCard(new EventCard("Rebel Logistics"));
        addCard(new EventCard("Banding Together"));
        addCard(new EventCard("Rebel Recruits"));
    }

    public EventDeck(List<EventCard> cards) {
        for (EventCard c : cards) {
            addCard(c);
        }
    }
}

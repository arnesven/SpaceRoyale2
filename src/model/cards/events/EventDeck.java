package model.cards.events;

import model.cards.*;

import java.util.List;

public class EventDeck extends Deck<EventCard> {

    public EventDeck() {
        for (int i = 0; i < 3; ++i) {
            addCard(new RebelLogisticsEventCard());
            addCard(new BandingTogetherEventCard());
            addCard(new RebelRecruitsEventCard());
        }
        addCard(new ViolentProtestsEventCard());
        addCard(new RiotsEventCard());
        addCard(new BoilingPointEventCard());
        for (int i = 0; i < 2; ++i) {
            addCard(new UnrelentingIllnessEventCard());
            addCard(new StrategicBlunderEventCard());
            addCard(new DisastrousMoraleEventCard());
            addCard(new RebelSabotageEventCard());
        }
        addCard(new UntimelySummonEventCard());
        addCard(new MistressOfMisfortuneEventCard());
        shuffle();
    }

    public EventDeck(List<EventCard> cards) {
        for (EventCard c : cards) {
            addCard(c);
        }
    }
}

package model.cards.events;

import model.cards.*;
import view.ScreenHandler;

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
        addCard(new AccordingToMyDesignEventCard());
        addCard(new MiraculousRecoveryEventCard());
        addCard(new PropagandaCampaignEventCard());
        addCard(new PrisonBreakEventCard());
        addCard(new SuperTitanEventCard());
        addCard(new ChampionOfLightEventCard());
        addCard(new SuddenDeathEventCard());
        addCard(new NewTechRollOutEventCard());
        addCard(new RescueCplCopperEventCard());
        addCard(new ImperialSpyEventCard());
        addCard(new NeutrinoBombsEventCard());
        addCard(new AdamantiumAlloyEventCard());
        addCard(new AdvancedLogisticsEventCard());
        addCard(new TachyonDrivesEventCard());
        addCard(new QuantumComputationEventCard());
        addCard(new DarkPowerEventCard());
        addCard(new WitchHuntEventCard());
        addCard(new TheResistanceEventcard());
        shuffle();
    }

    public EventDeck(List<EventCard> cards) {
        for (EventCard c : cards) {
            addCard(c);
        }
    }

    public void drawYourself(ScreenHandler screenHandler, int x, int y) {
        if (!isEmpty()) {
            screenHandler.drawText(".--.", x, y);
            screenHandler.drawText("|Ev|" + size(), x, y + 1);
            screenHandler.drawText("|nt|", x, y + 2);
            screenHandler.drawText("'¨¨'", x, y + 2);
        }
    }
}

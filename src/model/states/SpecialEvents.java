package model.states;

import model.Model;
import model.Player;
import model.cards.special.*;
import util.MyRandom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialEvents implements Serializable {

    public Map<Player, SpecialEventCard> map = new HashMap<>();

    private final List<SpecialEventCard> otherSpecialEvents = new ArrayList<>(List.of(
            new ParadeSpecialEventCard(),
            new DemonstrationSpecialEventCard(),
            new AudienceSpecialEventCard(),
            new CouncilOfTenSpecialEventCard()
    ));

    public void placeCards(Model model, List<SpecialEventCard> specials) {
        for (SpecialEventCard specialEvent : specials) {
            while (true) {
                Player p = MyRandom.sample(model.getPlayers());
                if (map.get(p) == null) {
                    place(p, specialEvent);
                    model.getScreenHandler().println(specialEvent.getName() + " is placed after " + p.getName() + ".");
                    break;
                }
            }
        }
    }

    public void place(Player player, SpecialEventCard specialEvent) {
        map.put(player, specialEvent);
    }

    public SpecialEventCard getCardForPlayer(Player player) {
        return map.get(player);
    }

    public void placeRandomSpecialEvents(Model model, int i) {
        List<SpecialEventCard> cardsToPlace = MyRandom.sampleN(i, otherSpecialEvents);
        otherSpecialEvents.removeAll(cardsToPlace);
        placeCards(model, cardsToPlace);
    }

    public void returnCard(SpecialEventCard specialEventCard) {
        removeCard(specialEventCard);
        otherSpecialEvents.add(specialEventCard);
    }

    public void removeCard(SpecialEventCard specialEventCard) {
        for (Map.Entry<Player, SpecialEventCard> entry : map.entrySet()) {
            if (entry.getValue() == specialEventCard) {
                map.remove(entry.getKey());
                return;
            }
        }
        throw new IllegalStateException("Tried removing a special event card that nobody had!");
    }

    public int getNumberOfCardsInPlay() {
        return map.size();
    }
}

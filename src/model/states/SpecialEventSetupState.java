package model.states;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.special.*;
import util.MyLists;
import util.MyRandom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpecialEventSetupState extends GameState {

    private final SpecialEventCard reorganizeCard = new ReorganizeSpecialEvent();

    private static final List<SpecialEventCard> otherSpecialEvents = List.of(
            new ParadeSpecialEventCard(),
            new DemonstrationSpecialEventCard(),
            new AudienceSpecialEventCard()
    );

    @Override
    public GameState run(Model model) {
        model.clearSpecialEvents();
        List<SpecialEventCard> cardsToUse = new ArrayList<>();
        cardsToUse.add(reorganizeCard);

        List<SpecialEventCard> otherCards = new ArrayList<>(otherSpecialEvents);

        if (model.getTurn() > 1) {
            if (model.getPlayers().size() < 8) {
                otherCards.remove(MyRandom.sample(otherCards));
            }
            if (model.getPlayers().size() == 5) {
                otherCards.remove(MyRandom.sample(otherCards));
            }
            cardsToUse.addAll(otherCards);
        }

        model.getScreenHandler().println("Special Event Setup. Selected cards: " +
                MyLists.commaAndJoin(cardsToUse, GameCard::getName));

        Collections.shuffle(cardsToUse);
        List<Player> playersLeft = new ArrayList<>(model.getPlayers());
        for (SpecialEventCard spec : cardsToUse) {
            Player p = MyRandom.sample(playersLeft);
            playersLeft.remove(p);
            model.placeSpecialEvent(p, spec);
            model.getScreenHandler().println(spec.getName() + " is placed after " + p.getName() + ".");
        }

        return new PlayerActionState();
    }
}

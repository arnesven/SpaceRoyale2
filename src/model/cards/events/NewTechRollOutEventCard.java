package model.cards.events;

import model.Model;
import model.Player;
import model.cards.DeckIsEmptyException;
import model.cards.GameCard;
import model.cards.tactics.TacticsCard;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewTechRollOutEventCard extends EventCard {
    public NewTechRollOutEventCard() {
        super("New Tech Roll-Out", false, "The current player draws and reveals a " +
                "card from the Tactics deck. Then go through the deck to find all copies of that card. " +
                "Distribute these Tactics cards to different players. Shuffle the tactics deck.");
    }

    @Override
    public void resolve(Model model, Player player) {
        TacticsCard tc = model.getTacticsDeck().drawOne();
        model.getScreenHandler().println(player.getName() + " draws a Tactics card. It's " + tc.getName() + ".");
        List<TacticsCard> cardsOfSameType = new ArrayList<>();
        List<TacticsCard> otherCards = new ArrayList<>();
        do {
            try {
                TacticsCard next = model.drawTacticsCard();
                if (next.getName().equals(tc.getName())) {
                    cardsOfSameType.add(next);
                } else {
                    otherCards.add(next);
                }
            } catch (DeckIsEmptyException die) {
                break;
            }
        } while (true);

        for (TacticsCard card : otherCards) {
            model.discardTacticsCards(card);
        }

        Set<Player> alreadyGivenTo = new HashSet<>();
        alreadyGivenTo.add(player);
        player.addCardToHand(tc);
        model.getScreenHandler().println("Found " + cardsOfSameType.size() + " other copies of " + tc.getName() + ".");
        for (TacticsCard tc2 : cardsOfSameType) {
            MultipleChoice multipleChoice = new MultipleChoice();
            for (Player p : model.getPlayersNotDefectors()) {
                if (!alreadyGivenTo.contains(p)) {
                    multipleChoice.addOption(p.getName(), (m, p2) -> {
                        p.addCardToHand(tc2);
                        m.getScreenHandler().println(p2.getName() + " gives " + p.getName() + " a " + tc2.getName() + ".");
                        alreadyGivenTo.add(p);
                    });
                }
            }
            if (multipleChoice.getNumberOfChoices() > 0) {
                multipleChoice.promptAndDoAction(model, "Who does " + player.getName() + " give the a card to?", player);
            }
        }

    }

    @Override
    public GameCard copy() {
        return new NewTechRollOutEventCard();
    }
}

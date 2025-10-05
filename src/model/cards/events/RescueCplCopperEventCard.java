package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.units.AgentUnitCard;
import model.cards.units.EmpireUnitCard;
import model.cards.units.UnitCard;
import util.MyLists;
import util.MyStringFunction;
import util.MyStrings;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public class RescueCplCopperEventCard extends EventCard {
    public RescueCplCopperEventCard() {
        super("Rescue Cpl Copper", false,
                "Each player may add unit cards from their hand to a face " +
                        "down pile. Shuffle the cards and reveal. Count only Ground " +
                        "Unit cards and Agents (counting as 3 each) if the sum is " +
                        "less than 10, move the war counter two steps toward the " +
                        "'Battle of Centralia' space.");
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println("Each player may commit cards to the event.");
        List<EmpireUnitCard> committedCards = new ArrayList<>();
        for (Player p : model.getPlayersStartingFrom(player)) {
            if (p.getUnitCardsInHand().isEmpty()) {
                model.getScreenHandler().println(p.getName() + " has no unit cards, skipping.");
            } else {
                MultipleChoice multipleChoice = new MultipleChoice();
                for (EmpireUnitCard eu : p.getUnitCardsInHand()) {
                    multipleChoice.addOption(eu.getName(), (m, p2) -> {
                        p2.removeUnitCardFromHand(eu);
                        committedCards.add(eu);
                    });
                }
                final boolean[] done = {false};
                multipleChoice.addOption("Pass", (m, p2) -> {
                    done[0] = true;
                });
                while (multipleChoice.getNumberOfChoices() > 1 || !done[0]) {
                    multipleChoice.promptAndDoAction(model, "Which card does " + p.getName() + " commit to the event?", p);
                    multipleChoice.removeSelectedOption();
                }
            }
        }
        model.getScreenHandler().println("Cards committed to rescuing Cpl Copper: " + MyLists.frequencyList(committedCards,
                UnitCard::getNameAndStrength));

        int empireStrength = MyLists.intAccumulate(committedCards, empireUnitCard -> {
           if (empireUnitCard.isGroundUnit()) {
               return empireUnitCard.getStrength();
           }
           if (empireUnitCard instanceof AgentUnitCard) {
               return 3;
           }
           return 0;
        });

        model.getScreenHandler().println("Empire strength total: " + empireStrength);
        model.getScreenHandler().println("Rebel strength: 10");
        if (empireStrength < 10) {
            model.getScreenHandler().println("Rescue attempt has failed. War counter retreats two steps.");
            model.retreatWarCounter();
            model.retreatWarCounter();
        } else {
            model.getScreenHandler().println("The rescue attempt is successful.");
        }
        model.discardEmpireCards(committedCards);
    }

    @Override
    public GameCard copy() {
        return new RescueCplCopperEventCard();
    }
}

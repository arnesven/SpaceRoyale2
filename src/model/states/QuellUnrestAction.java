package model.states;

import model.Model;
import model.Player;
import model.cards.AgentUnitCard;
import model.cards.EmpireUnitCard;
import model.cards.UnitCard;
import util.MyLists;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuellUnrestAction {
    public static void doAction(Model model, Player player) {
        List<Player> involvedPlayers = new ArrayList<>();
        MultipleChoice multipleChoice = new MultipleChoice();
        for (Player p : model.getPlayers()) {
            if (p != player && isOnCentralia(model, p)) {
                multipleChoice.addOption(p.getName(), (_, _) -> involvedPlayers.add(p));
            }
        }

        boolean first = true;
        boolean[] isDone = new boolean[]{false};
        do {
            multipleChoice.promptAndDoAction(model, "Which other player do you involve in the action?", player);
            multipleChoice.removeSelectedOption();
            if (first) {
                multipleChoice.addOption("Done", (_, _) -> isDone[0] = true);
                first = false;
            }
        } while (!isDone[0] && involvedPlayers.size() < 3 && multipleChoice.getNumberOfChoices() > 1);


        involvedPlayers.addFirst(player);
        model.getScreenHandler().println(MyLists.commaAndJoin(involvedPlayers, Player::getName) +
                " will attempt to quell the unrest.");
        model.getScreenHandler().println("Each player must contribute one Empire Unit Card.");

        List<EmpireUnitCard> contributedCards = new ArrayList<>();
        for (Player p : involvedPlayers) {
            if (p.getUnitCardsInHand().isEmpty()) {
                model.getScreenHandler().println(p.getName() + " doesn't not have any Unit card, drawing 1 from the deck.");
                p.drawUnitCard(model);
                contributedCards.add(p.getUnitCardsInHand().removeFirst());
            } else {
                multipleChoice = new MultipleChoice();
                for (EmpireUnitCard eu : p.getUnitCardsInHand()) {
                    multipleChoice.addOption(eu.getName(), (_, _) -> {
                        contributedCards.add(eu);
                        p.removeUnitCardFromHand(eu);
                        model.getScreenHandler().println(p.getName() + " contributes a card, face down.");
                    });
                }
                multipleChoice.promptAndDoAction(model, "Which card does " + p.getName() + " contribute?", p);
            }
        }
        Collections.shuffle(contributedCards);
        
        model.getScreenHandler().println("The contributed cards are: ");
        model.getScreenHandler().println(MyLists.frequencyList(contributedCards, UnitCard::getNameAndStrength));
        if (MyLists.all(contributedCards, eu -> eu.isGroundUnit() || eu instanceof AgentUnitCard)) {
            int reduction = contributedCards.size()-1;
            model.getScreenHandler().println("All cards are Ground Units or Agents, unrest is reduced by " + reduction + ".");
            model.increaseUnrest(-reduction);
        } else {
            model.getScreenHandler().println("The attempt to quell the unrest has failed. Unrest increases instead!");
            model.increaseUnrest(1);
        }

        model.discardEmpireCards(contributedCards);
    }

    private static boolean isOnCentralia(Model model, Player player) {
        return model.getCentralia() == player.getCurrentLocation();
    }
}

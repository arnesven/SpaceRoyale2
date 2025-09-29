package model.states;

import model.Model;
import model.Player;
import model.cards.DeckIsEmptyException;
import model.cards.EmpireUnitCard;
import model.cards.GameCard;
import model.cards.UnitCard;
import util.Arithmetics;
import util.MyLists;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrestAction {
    public static void doAction(Model model, Player player) {
        Player[] targetedPlayer = new Player[1];
        MultipleChoice multipleChoice = new MultipleChoice();
        for (Player p : model.getPlayers()) {
            if (p != player) {
                multipleChoice.addOption(p.getName(), (_, _) -> targetedPlayer[0] = p);
            }
        }
        multipleChoice.promptAndDoAction(model, "Who do you want to attempt to arrest?", player);

        model.getScreenHandler().println(player.getName() + " is attempting to arrest " + targetedPlayer[0].getName() + ".");
        model.getScreenHandler().println("All players may contribute cards to this attempt.");
        model.getScreenHandler().println("Ship units help to enforce the arrest, ground units prevent it.");

        List<EmpireUnitCard> contributedCards = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        int index = model.getPlayers().indexOf(player);
        for (int i = 0; i < model.getPlayers().size(); ++i) {
            players.add(model.getPlayers().get(index));
            index = Arithmetics.incrementWithWrap(index, model.getPlayers().size());
        }

        for (Player p : players) {
            multipleChoice = new MultipleChoice();
            for (EmpireUnitCard eu : p.getUnitCardsInHand()) {
                multipleChoice.addOption(eu.getName(), (m, p2) -> {
                    model.getScreenHandler().println(p2.getName() + " contributes one card, face down.");
                    p2.removeUnitCardFromHand(eu);
                    contributedCards.add(eu);
                });
            }
            multipleChoice.addOption("Pass", (_, _) -> {});
            multipleChoice.promptAndDoAction(model, "What card does " + p.getName() + " contribute?", p);
            if (p == targetedPlayer[0] && multipleChoice.getSelectedOptionIndex() != multipleChoice.getNumberOfChoices()) {
                model.getScreenHandler().println("Since " + p.getName() + " is the target of the arrest, one additional card may be played.");
                multipleChoice.removeSelectedOption();
                multipleChoice.promptAndDoAction(model, "What card do you want to contribute?", p);
            }
        }

        try {
            EmpireUnitCard extra = model.drawCommunalEmpireUnitCard();
            contributedCards.add(extra);
            model.getScreenHandler().println("Adding one extra card from Common Empire Unit deck.");
        } catch (DeckIsEmptyException die) {
            model.getScreenHandler().println("No Common Empire Unit deck, skipping extra card.");
        }

        Collections.shuffle(contributedCards);
        model.getScreenHandler().println("The contributed cards are: " + MyLists.frequencyList(contributedCards, GameCard::getName));
        int spaceTotal = MyLists.filter(contributedCards, UnitCard::isSpaceUnit).size();
        int groundTotal = MyLists.filter(contributedCards, UnitCard::isGroundUnit).size();
        model.getScreenHandler().println("There are " + spaceTotal + " Space Units and " + groundTotal + " Ground Units.");
        if (spaceTotal > groundTotal) {
            model.getScreenHandler().println("The arrest attempt is successful. " + targetedPlayer[0].getName() + " is moved to the Prison Planet!");
            targetedPlayer[0].moveToLocation(model.getPrisonPlanet());
        } else {
            model.getScreenHandler().println("The arrest attempt has failed.");
        }
        model.discardEmpireCards(contributedCards);
    }
}

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

    public static void arrestAction(Model model, Player player) {
        Player[] targetedPlayer = new Player[1];
        MultipleChoice multipleChoice = new MultipleChoice();
        for (Player p : MyLists.filter(model.getPlayers(), p -> p.getCurrentLocation() != model.getPrisonPlanet())) {
            if (p != player) {
                multipleChoice.addOption(p.getName(), (_, _) -> targetedPlayer[0] = p);
            }
        }
        multipleChoice.promptAndDoAction(model, "Who do you want to attempt to arrest?", player);

        model.getScreenHandler().println(player.getName() + " is attempting to arrest " + targetedPlayer[0].getName() + ".");
        model.getScreenHandler().println("All players may contribute cards to this attempt.");
        model.getScreenHandler().println("Ship units help to enforce the arrest, ground units prevent it.");
        boolean arrestSucceeded = arrestOrFleeAttempt(model, player, targetedPlayer[0], "is the target of the arrest");
        if (arrestSucceeded) {
            model.getScreenHandler().println("The arrest attempt is successful. " + targetedPlayer[0].getName() + " is moved to the Prison Planet!");
            targetedPlayer[0].moveToLocation(model.getPrisonPlanet());
        } else {
            model.getScreenHandler().println("The arrest attempt has failed.");
        }
    }

    private static boolean arrestOrFleeAttempt(Model model, Player initiator, Player target, String extraCardString) {

        List<EmpireUnitCard> contributedCards = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        int index = model.getPlayers().indexOf(initiator);
        for (int i = 0; i < model.getPlayers().size(); ++i) {
            players.add(model.getPlayers().get(index));
            index = Arithmetics.incrementWithWrap(index, model.getPlayers().size());
        }

        MultipleChoice multipleChoice;
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
            if (p == target && multipleChoice.getSelectedOptionIndex() != multipleChoice.getNumberOfChoices()) {
                model.getScreenHandler().println("Since " + p.getName() + " " + extraCardString + ", one additional card may be played.");
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
        model.discardEmpireCards(contributedCards);
        if (spaceTotal > groundTotal) {
            return true;
        }
        return false;
    }

    public static void releaseFromPrison(Model model, Player player) {
        Player[] targetedPlayer = new Player[1];
        MultipleChoice multipleChoice = new MultipleChoice();
        for (Player p : MyLists.filter(model.getPlayers(), p -> p.getCurrentLocation() == model.getPrisonPlanet())) {
            multipleChoice.addOption(p.getName(), (_, _) -> targetedPlayer[0] = p);
        }
        multipleChoice.promptAndDoAction(model, "Who do you want to attempt to break out of prison?", player);
        model.getScreenHandler().println(player.getName() + " is attempting to break " + targetedPlayer[0].getName() + " out of prison.");
        innerEscapeFromPrison(model, player, targetedPlayer[0]);
    }

    private static void innerEscapeFromPrison(Model model, Player initiator, Player player) {
        model.getScreenHandler().println("All players may contribute cards to this attempt.");
        model.getScreenHandler().println("Ground units will help the escapee, space units will hinder the escapee.");
        boolean arrestSucceeded = arrestOrFleeAttempt(model, initiator, player, "is the one attempting the escape");
        if (arrestSucceeded) {
            model.getScreenHandler().println("The escape attempt has failed.");
        } else {
            model.getScreenHandler().println("The escape attempt is successful. " + player.getName() + " escapes the Prison Planet!");
            player.moveToLocation(model.getCentralia());
        }
    }

    public static void escapeFromPrison(Model model, Player player) {
        model.getScreenHandler().println(player.getName() + " attempts to escape the Prison Planet.");
        innerEscapeFromPrison(model, player, player);
    }

    public static boolean anybodyOnPrisonPlanet(Model model) {
        return MyLists.any(model.getPlayers(), p -> p.getCurrentLocation() == model.getPrisonPlanet());
    }

    public static boolean anybodyArrestable(Model model, Player current) {
        return MyLists.any(model.getPlayers(), p -> p != current && p.getCurrentLocation() != model.getPrisonPlanet());
    }
}

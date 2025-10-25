package model.cards.special;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.units.AgentUnitCard;
import model.cards.units.EmpireUnitCard;
import util.MyLists;
import util.MyPair;
import util.MyRandom;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AudienceSpecialEventCard extends SpecialEventCard {
    public AudienceSpecialEventCard() {
        super("Audience");
    }

    @Override
    public void resolve(Model model) {

        List<Player> sorted = new ArrayList<>(MyLists.filter(model.getPlayers(),
                p -> p.getCurrentLocation() == model.getCentralia()));

        if (sorted.isEmpty()) {
            model.getScreenHandler().println("No players on Centralia.");
            return;
        }

        List<Player> playerOrder = model.getPlayersStartingFrom(model.getCurrentPlayer());

        sorted.sort((o1, o2) -> score(o1, playerOrder) - score(o2, playerOrder));

        model.getScreenHandler().println("Result of audience:");
        for (Player player : sorted) {
            model.getScreenHandler().println(player.getName() + ": " + player.getEmperorInfluence() + " EI, "
                     + player.getPopularInfluence() + " PI, " + playerOrder.indexOf(player) + " place.");
        }

        for (Player player : sorted) {
            MultipleChoice multipleChoice = new MultipleChoice();
            multipleChoice.addOption("Have Audience", this::haveAudience);
            multipleChoice.addOption("Pass", (_, _) -> {});
            multipleChoice.promptAndDoAction(model, "Does " + player.getName() + " want an audience with the Emperor?", player);
            if (multipleChoice.getSelectedOptionIndex() == 1) {
                break;
            }
        }

    }

    private void haveAudience(Model model, Player player) {
        int dieRoll =MyRandom.rollD10();
        model.getScreenHandler().println(player.getName() + " rolls the die, it's a " + dieRoll + ".");
        if (MyLists.any(player.getUnitCardsInHand(), uc -> uc instanceof AgentUnitCard)) {
            model.getScreenHandler().println(player.getName() + " reveals an Agent card to add 3 to the die roll");
            dieRoll += 3;
        }
        if (dieRoll < 6) {
            model.getScreenHandler().println("The Emperor is unimpressed by " + player.getName() + ".");
            model.getScreenHandler().println(player.getName() + " loses 1 Emperor Influence.");
            player.addToEmperorInfluence(-1);
        } else {
            model.getScreenHandler().println("The Emperor is pleased by " + player.getName() + ".");
            model.getScreenHandler().println(player.getName() + " gains 1 Emperor Influence.");
            player.addToEmperorInfluence(1);
        }
    }

    private int score(Player player, List<Player> playerOrder) {
        return player.getEmperorInfluence() * 1000 + player.getPopularInfluence() * 100 -
                playerOrder.indexOf(player);
    }

    @Override
    public GameCard copy() {
        return new AudienceSpecialEventCard();
    }
}

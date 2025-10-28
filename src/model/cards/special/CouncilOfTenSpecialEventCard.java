package model.cards.special;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.tactics.TacticsCard;
import model.cards.units.EmpireUnitCard;
import util.MyLists;
import util.MyRandom;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public class CouncilOfTenSpecialEventCard extends SpecialEventCard {
    private static final int TARGET_NUMBER = 5;

    public CouncilOfTenSpecialEventCard() {
        super("Council of Ten");
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
        model.getScreenHandler().println("Result of Council of Ten:");
        for (Player player : sorted) {
            model.getScreenHandler().println(player.getName() + ": " + player.getPopularInfluence() + " PI, "
                    + player.getTotalCardsInHand() + " cards, " + (player.getTotalCardsInHand() < 2 ? "(too few cards)" : ""));
        }

        for (Player player : MyLists.filter(sorted, p -> p.getTotalCardsInHand() >= 2)) {
            MultipleChoice multipleChoice = new MultipleChoice();
            multipleChoice.addOption("Meet Council of Ten", this::meetCouncilOfTen);
            multipleChoice.addOption("Pass", (_, _) -> {
            });
            multipleChoice.promptAndDoAction(model, "Does " + player.getName() + " want to meet the Council of Ten?", player);
            if (multipleChoice.getSelectedOptionIndex() == 1) {
                break;
            }
        }
    }

    private void meetCouncilOfTen(Model model, Player player) {
        MultipleChoice multipleChoice = new MultipleChoice();
        List<GameCard> selectedCards = new ArrayList<>();
        for (EmpireUnitCard c : player.getUnitCardsInHand()) {
            multipleChoice.addOption(c.getName(), (m, p) -> {
                selectedCards.add(c);
                p.discardCard(m, c);
            });
        }
        for (TacticsCard t : player.getTacticsCardsInHand()) {
            multipleChoice.addOption(t.getName(), (m, p) -> {
                selectedCards.add(t);
                p.discardCard(m, t);
            });
        }
        while (selectedCards.size() < 2) {
            multipleChoice.promptAndDoAction(model, "Select 2 cards to discard.", player);
        }
        int dieRoll = MyRandom.rollD10();
        model.getScreenHandler().println("Rolling a die, it's a " + dieRoll + ".");
        if (dieRoll >= TARGET_NUMBER) {
            model.getScreenHandler().println("The Council of Ten is pleased with the donations.");
            model.getScreenHandler().println(player.getName() + " gains 2 Popular Influence.");
            player.addToPopularInfluence(2);
        } else {
            model.getScreenHandler().println("The Council of Ten scoffs at your pathetic attempts to impress them.");
        }
    }

    private int score(Player player, List<Player> playerOrder) {
        return player.getPopularInfluence() * 1000 + player.getTotalCardsInHand();
    }

    @Override
    public GameCard copy() {
        return new CouncilOfTenSpecialEventCard();
    }
}

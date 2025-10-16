package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.tactics.TacticsCard;
import model.cards.units.EmpireUnitCard;
import util.MyLists;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public class DarkPowerEventCard extends EventCard {
    private static final int NO_OF_CARDS = 4;

    public DarkPowerEventCard() {
        super("A Dark Power", false,
                "The player or players with the least Empire Influence must " +
                        "immediately discard " + NO_OF_CARDS + " Unit and/or Tactics cards.");
    }

    @Override
    public void resolve(Model model, Player player) {
        int minPi = MyLists.minimum(model.getPlayersNotDefectors(), Player::getPopularInfluence);
        for (Player p : MyLists.filter(model.getPlayersNotDefectors(), p2 -> p2.getPopularInfluence() == minPi)) {
            model.getScreenHandler().println(p.getName() + " has the least Popular Influence (" + minPi + ").");
            playerDiscardsCards(model, p);
        }
    }

    private void playerDiscardsCards(Model model, Player player) {
        List<EmpireUnitCard> unitsToDiscard = new ArrayList<>();
        List<TacticsCard> tacticsToDiscard = new ArrayList<>();
        if (player.getTotalCardsInHand() <= NO_OF_CARDS) {
            model.getScreenHandler().println(player.getName() + " has " + NO_OF_CARDS + " or less cards, discarding entire hand.");
            unitsToDiscard.addAll(player.getUnitCardsInHand());
            tacticsToDiscard.addAll(player.getTacticsCardsInHand());
        } else {
            model.getScreenHandler().println(player.getName() + " must discard " + NO_OF_CARDS + " Unit and/or Tactics cards.");
            MultipleChoice multipleChoice = new MultipleChoice();
            for (EmpireUnitCard eu : player.getUnitCardsInHand()) {
                multipleChoice.addOption(eu.getName(), (m, p) -> {
                    unitsToDiscard.add(eu);
                    model.getScreenHandler().println(eu.getName() + " selected, " + cardsLeft(unitsToDiscard, tacticsToDiscard) + " left to select.");
                });
            }
            for (TacticsCard tc : player.getTacticsCardsInHand()) {
                multipleChoice.addOption(tc.getName(), (m, p) -> {
                    tacticsToDiscard.add(tc);
                    model.getScreenHandler().println(tc.getName() + " selected, " + cardsLeft(unitsToDiscard, tacticsToDiscard) + " left to select.");
                });
            }
            while (unitsToDiscard.size() + tacticsToDiscard.size() < NO_OF_CARDS) {
                multipleChoice.promptAndDoAction(model, "Discard which card?", player);
                multipleChoice.removeSelectedOption();
            }
        }

        for (EmpireUnitCard eu : unitsToDiscard) {
            player.discardCard(model, eu);
        }
        for (TacticsCard tc : tacticsToDiscard) {
            player.discardCard(model, tc);
        }
    }

    private int cardsLeft(List<EmpireUnitCard> unitsToDiscard, List<TacticsCard> tacticsToDiscard) {
        return NO_OF_CARDS - unitsToDiscard.size() - tacticsToDiscard.size();
    }

    @Override
    public GameCard copy() {
        return new DarkPowerEventCard();
    }
}

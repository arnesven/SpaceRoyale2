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


    public DarkPowerEventCard() {
        super("A Dark Power", false,
                "The player with the least Empire Influence must immediately discard 4 Unit and/or Tactics cards. " +
                        "If several players are tied, they each discard 2 cards instead.");
    }

    @Override
    public void resolve(Model model, Player player) {
        int minPi = MyLists.minimum(model.getPlayersNotDefectors(), Player::getPopularInfluence);
        List<Player> targets = MyLists.filter(model.getPlayersNotDefectors(), p2 -> p2.getPopularInfluence() == minPi);
        int cardsToDiscard = 4;
        if (targets.size() > 1) {
            cardsToDiscard = 2;
        }

        for (Player p : targets) {
            model.getScreenHandler().println(p.getName() + " has the least Popular Influence (" + minPi + ").");
            playerDiscardsCards(model, p, cardsToDiscard);
        }
    }

    private void playerDiscardsCards(Model model, Player player, int numberOfCards) {
        List<EmpireUnitCard> unitsToDiscard = new ArrayList<>();
        List<TacticsCard> tacticsToDiscard = new ArrayList<>();
        if (player.getTotalCardsInHand() <= numberOfCards) {
            model.getScreenHandler().println(player.getName() + " has " + numberOfCards + " or less cards, discarding entire hand.");
            unitsToDiscard.addAll(player.getUnitCardsInHand());
            tacticsToDiscard.addAll(player.getTacticsCardsInHand());
        } else {
            model.getScreenHandler().println(player.getName() + " must discard " + numberOfCards + " Unit and/or Tactics cards.");
            MultipleChoice multipleChoice = new MultipleChoice();
            for (EmpireUnitCard eu : player.getUnitCardsInHand()) {
                multipleChoice.addOption(eu.getName(), (m, p) -> {
                    unitsToDiscard.add(eu);
                    model.getScreenHandler().println(eu.getName() + " selected, " + cardsLeft(numberOfCards, unitsToDiscard, tacticsToDiscard) + " left to select.");
                });
            }
            for (TacticsCard tc : player.getTacticsCardsInHand()) {
                multipleChoice.addOption(tc.getName(), (m, p) -> {
                    tacticsToDiscard.add(tc);
                    model.getScreenHandler().println(tc.getName() + " selected, " + cardsLeft(numberOfCards, unitsToDiscard, tacticsToDiscard) + " left to select.");
                });
            }
            while (unitsToDiscard.size() + tacticsToDiscard.size() < numberOfCards) {
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

    private int cardsLeft(int numberOfCards, List<EmpireUnitCard> unitsToDiscard, List<TacticsCard> tacticsToDiscard) {
        return numberOfCards - unitsToDiscard.size() - tacticsToDiscard.size();
    }

    @Override
    public GameCard copy() {
        return new DarkPowerEventCard();
    }
}

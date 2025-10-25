package model.cards.special;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.units.EmpireUnitCard;
import model.cards.units.UnitCard;
import util.MyLists;
import util.MyPair;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public class ParadeSpecialEventCard extends SpecialEventCard {

    public ParadeSpecialEventCard() {
        super("Parade");
    }

    @Override
    public void resolve(Model model) {
        List<MyPair<Player, Integer>> contenders = new ArrayList<>();
        for (Player player : MyLists.filter(model.getPlayersStartingFrom(model.getCurrentPlayer()),
                p -> p.getCurrentLocation() == model.getCentralia())) {
            MyPair<Player, Integer> pair = new MyPair<>(player, 0);
            MultipleChoice multipleChoice = new MultipleChoice();
            for (EmpireUnitCard c : MyLists.filter(player.getUnitCardsInHand(), UnitCard::isGroundUnit)) {
                multipleChoice.addOption(c.getName(), (m, p) -> pair.second += c.getStrength());
            }
            boolean[] done = new boolean[]{false};
            multipleChoice.addOption("Pass", (_, _) -> { done[0] = true; });
            do {
                multipleChoice.promptAndDoAction(model, "Does " + player.getName() +
                        " want to reveal a Ground Unit card from their hand?", player);
                multipleChoice.removeSelectedOption();
            } while (!done[0]);
            if (pair.second != null) {
                contenders.add(pair);
            }
        }

        if (contenders.isEmpty()) {
            model.getScreenHandler().println("Nobody participated in the Parade.");
            return;
        }

        contenders.sort((o1, o2) -> score(o2) - score(o1));
        model.getScreenHandler().println("Result of parade:");
        for (MyPair<Player, Integer> pair : contenders) {
            model.getScreenHandler().println(pair.first.getName() + ": " + pair.second +
                    ", (" + pair.first.getEmperorInfluence() + " EI)");
        }

        for (MyPair<Player, Integer> winner : contenders) {
            if (score(winner) == score(contenders.getFirst())) {
                model.getScreenHandler().println("The emperor rewards " + winner.first.getName() + " with 1 Emperor Influence.");
                winner.first.addToEmperorInfluence(1);
            }
        }
    }

    private int score(MyPair<Player, Integer> pair) {
        return pair.second * 100 - pair.first.getEmperorInfluence();
    }

    @Override
    public GameCard copy() {
        return new ParadeSpecialEventCard();
    }
}

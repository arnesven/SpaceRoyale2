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
import java.util.Comparator;
import java.util.List;

public class DemonstrationSpecialEventCard extends SpecialEventCard {
    public DemonstrationSpecialEventCard() {
        super("Demonstration");
    }

    @Override
    public void resolve(Model model) {
        List<MyPair<Player, EmpireUnitCard>> contenders = new ArrayList<>();
        for (Player player : MyLists.filter(model.getPlayersStartingFrom(model.getCurrentPlayer()),
                p -> p.getCurrentLocation() == model.getCentralia())) {
            MultipleChoice multipleChoice = new MultipleChoice();
            MyPair<Player, EmpireUnitCard> pair = new MyPair<>(player, null);
            for (EmpireUnitCard c : MyLists.filter(player.getUnitCardsInHand(), UnitCard::isSpaceUnit)) {
                multipleChoice.addOption(c.getName(), (m, p) -> pair.second = c);
            }
            multipleChoice.addOption("Pass", (_, _) -> {});
            multipleChoice.promptAndDoAction(model, "Does " + player.getName() +
                    " want to reveal a Space Unit card from their hand?", player);
            if (pair.second != null) {
                contenders.add(pair);
            }
        }

        if (contenders.isEmpty()) {
            model.getScreenHandler().println("Nobody participated in the Demonstration.");
            return;
        }

        contenders.sort((o1, o2) -> score(o2) - score(o1));

        model.getScreenHandler().println("Result of demonstration:");
        for (MyPair<Player, EmpireUnitCard> pair : contenders) {
            model.getScreenHandler().println(pair.first.getName() + ": " + pair.second.getNameAndStrength() +
                    ", (" + pair.first.getEmperorInfluence() + " EI)");
        }

        for (MyPair<Player, EmpireUnitCard> winner : contenders) {
            if (score(winner) == score(contenders.getFirst())) {
                model.getScreenHandler().println("The emperor rewards " + winner.first.getName() + " with 1 Emperor Influence.");
                winner.first.addToEmperorInfluence(1);
            }
        }
    }

    private int score(MyPair<Player, EmpireUnitCard> o1) {
        return o1.second.getStrength() * 100 - o1.first.getEmperorInfluence();
    }

    @Override
    public GameCard copy() {
        return new DemonstrationSpecialEventCard();
    }
}

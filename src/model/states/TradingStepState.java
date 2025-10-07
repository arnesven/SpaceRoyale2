package model.states;

import model.Model;
import model.Player;
import model.cards.tactics.TacticsCard;
import model.cards.units.EmpireUnitCard;
import view.MultipleChoice;

public class TradingStepState extends GameState {
    @Override
    public GameState run(Model model) {
        model.getScreenHandler().println("Trading Step.");
        for (Player p : model.getPlayersNotDefectors()) {
            MultipleChoice multipleChoice = new MultipleChoice();
            for (Player p2 : model.getPlayersNotDefectors()) {
                if (p != p2) {
                    multipleChoice.addOption(p2.getName(), (m, x) -> {
                        giveCardsToPlayer(model, x, p2);
                    });
                }
            }
            boolean[] done = new boolean[]{false};
            multipleChoice.addOption("Pass", (_,_) ->{done[0] = true;});
            while (!done[0] && p.getTotalCardsInHand() > 0) {
                multipleChoice.promptAndDoAction(model, "Does " + p.getName() +
                        " want to give cards to another player?", p);
            }
        }

        for (Player p : model.getPlayersNotDefectors()) {
            PlayerActionState.discardIfOverLimit(model, p);
        }
        model.drawBoard();
        return new PlayerActionState();
    }

    private void giveCardsToPlayer(Model model, Player from, Player to) {
        MultipleChoice multipleChoice = new MultipleChoice();
        boolean[] done = new boolean[]{false};
        for (EmpireUnitCard eu : from.getUnitCardsInHand()) {
            multipleChoice.addOption(eu.getName(), (m, _) -> {
                to.addCardToHand(eu);
                from.removeUnitCardFromHand(eu);
                m.getScreenHandler().println(from.getName() + " gives " + eu.getName() + " to " + to.getName() + ".");
            });
        }
        for (TacticsCard tc : from.getTacticsCardsInHand()) {
            multipleChoice.addOption(tc.getName(), (m, _) -> {
                to.addCardToHand(tc);
                from.removeTacticsCardFromHand(tc);
                m.getScreenHandler().println(from.getName() + " gives " + tc.getName() + " to " + to.getName() + ".");
            });
        }
        multipleChoice.addOption("Pass", (_, _) -> {
            done[0] = true;
        });
        while (multipleChoice.getNumberOfChoices() > 0 && !done[0]) {
           multipleChoice.promptAndDoAction(model, "Give away chich card?", from);
           multipleChoice.removeSelectedOption();
        }
    }
}

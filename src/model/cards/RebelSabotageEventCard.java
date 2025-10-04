package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public class RebelSabotageEventCard extends EventCard {
    public RebelSabotageEventCard() {
        super("Rebel Sabotage", false,
                "The current player looks at all Empire Unit cards commited to Battle and discards half of them (rounded up).");
    }

    @Override
    public void resolve(Model model, Player player) {
        final BattleBoard[] selectedBattle = {null};
        MultipleChoice multipleChoice = new MultipleChoice();
        for (BattleBoard bb : model.getBattles()) {
            multipleChoice.addOption(bb.getName() + " (" + bb.getEmpireUnits().size() + " cards)", (m, _) -> {
                m.getScreenHandler().println("Discarding Empire Units from " + bb.getName() + ".");
                selectedBattle[0] = bb;
            });
        }
        multipleChoice.promptAndDoAction(model, "Discard Empire Unit cards from which battle?", player);
        assert selectedBattle[0] != null;

        List<EmpireUnitCard> selectedCards = new ArrayList<>();
        multipleChoice = new MultipleChoice();
        for (EmpireUnitCard eu : selectedBattle[0].getEmpireUnits()) {
            multipleChoice.addOption(eu.getName(), (m, p) -> {
                selectedCards.add(eu);
                selectedBattle[0].removeUnit(eu);
            });
        }
        while (selectedCards.size()*2 < selectedBattle[0].getEmpireUnits().size()) {
            multipleChoice.promptAndDoAction(model, "Discard which card?", player);
            multipleChoice.removeSelectedOption();
        }
        model.getScreenHandler().println("Discarding " + selectedCards.size() + " Empire unit cards from " +
                selectedBattle[0].getName() + ".");
        model.discardEmpireCards(selectedCards);
    }

    @Override
    public GameCard copy() {
        return new RebelSabotageEventCard();
    }
}

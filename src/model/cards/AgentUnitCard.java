package model.cards;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import view.MultipleChoiceAction;

public class AgentUnitCard extends EmpireUnitCard {
    public AgentUnitCard() {
        super("Agent", 0, false);
    }

    @Override
    public EmpireUnitCard copy() {
        return new AgentUnitCard();
    }

    public void useToPeek(Model model, Player player, BattleBoard bb) {
        model.getScreenHandler().println(player.getName() + " peeks at the Rebel Unit Cards on " + bb.getName() + ".");
        bb.printRebelUnits(model);
        player.discardCard(model, this);
    }
}

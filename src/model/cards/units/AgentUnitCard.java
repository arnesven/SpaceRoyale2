package model.cards.units;

import model.Model;
import model.Player;
import model.board.BattleBoard;

public class AgentUnitCard extends EmpireUnitCard {
    public AgentUnitCard() {
        super("Agent", 0, false);
    }

    @Override
    public EmpireUnitCard copy() {
        return new AgentUnitCard();
    }

    @Override
    public boolean isGroundUnit() {
        return false;
    }

    @Override
    public boolean isSpaceUnit() {
        return false;
    }

    public void useToPeek(Model model, Player player, BattleBoard bb) {
        model.getScreenHandler().println(player.getName() + " peeks at the Rebel Unit Cards on " + bb.getName() + ".");
        bb.printRebelUnits(model);
        bb.printRebelTally(model);
        player.discardCard(model, this);
    }
}

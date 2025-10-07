package model.cards.units;

import model.DefectedPlayer;
import model.Model;
import model.board.BattleBoard;
import model.cards.GameCard;
import view.MultipleChoice;

public class RebelOperativeUnitCard extends RebelUnitCard {
    public RebelOperativeUnitCard() {
        super("Operative", 0, false);
    }

    @Override
    public boolean isGroundUnit() {
        return false;
    }

    @Override
    public boolean isSpaceUnit() {
        return false;
    }

    @Override
    public GameCard copy() {
        return new RebelOperativeUnitCard();
    }

    public void askToUse(Model model, BattleBoard bb, DefectedPlayer defectedPlayer) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption("Use Operative", (m, p) -> {
            RebelOperativeUnitCard.this.peek(model, bb, defectedPlayer);
        });
        multipleChoice.addOption("Skip", (_,_) -> {});
        multipleChoice.promptAndDoAction(model, "Do you want to play an Operative Card?", defectedPlayer);
    }

    private void peek(Model model, BattleBoard bb, DefectedPlayer player) {
        model.getScreenHandler().println(player.getName() + " peeks at all Unit Cards on " + bb.getName() + ".");
        bb.printRebelUnits(model);
        bb.printEmpireUnits(model);
        bb.printTallies(model);
        player.discardCard(model, this);
    }
}

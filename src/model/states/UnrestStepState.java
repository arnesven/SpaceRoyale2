package model.states;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.alignment.AlignmentCard;
import model.cards.alignment.RebelAlignmentCard;
import model.cards.events.OppresiveBureaucracyEventCard;
import model.cards.tactics.SecurityForcesCard;
import model.cards.tactics.TacticsCard;
import util.MyLists;
import util.MyRandom;
import view.MultipleChoice;

public class UnrestStepState extends GameState {
    @Override
    public GameState run(Model model) {
        int dieRoll = MyRandom.rollD10();
        final int[] modified = new int[]{dieRoll};
        println(model, "Unrest step - the die is rolled, it's a " + dieRoll + ".");
        StringBuilder extra = new StringBuilder();
        AlignmentCard alignmentCard = model.drawBattleChanceCard();
        println(model, "Drawing Battle Chance card, it's " + alignmentCard.getName() + ".");
        if (alignmentCard instanceof RebelAlignmentCard) {
            extra.append(" +3");
            modified[0] += 3;
        }

        for (Player p : model.getPlayersNotDefectors()) {
            TacticsCard secForces = MyLists.find(p.getTacticsCardsInHand(), tc -> tc instanceof SecurityForcesCard);
            if (secForces != null) {
                MultipleChoice multipleChoice = new MultipleChoice();
                multipleChoice.addOption("Play card to add 2", (m, p2) -> {
                    extra.append(" +2 (Security Forces)");
                    modified[0] += 2;
                    BattleBoard.possiblyDiscardTacticsCard(m, p2, secForces);
                });
                multipleChoice.addOption("Play card to subtract 2", (m, p2) -> {
                    extra.append(" -2 (Security Forces)");
                    modified[0] -= 2;
                    BattleBoard.possiblyDiscardTacticsCard(m, p2, secForces);
                });
                multipleChoice.addOption("Don't play card", (_, _) -> {
                });
                multipleChoice.promptAndDoAction(model, "Does " + p.getName() + " play " + secForces.getName() +
                        " to modify the die roll by +2 or -2?", p);
            }
        }

        if (MyLists.any(model.getEventCardsInPlay(),
                ev -> ev instanceof OppresiveBureaucracyEventCard)) {
            extra.append(" +1 (Oppressive Bureaucracy)");
            modified[0] += 1;
        }
        if (extra.isEmpty()) {
            print(model, "The result is " + dieRoll + ", ");
        } else {
            print(model, "The result is " + dieRoll + extra + " = " + modified[0] + ", ");
        }

        if (modified[0] <= 0) {
            println(model, "Unrest decreases by one.");
            model.increaseUnrest(-1);
        } else if (modified[0] <= 5) {
            println(model, "no effect.");
        } else if (modified[0] <= 9) {
            println(model, "Unrest increases by one.");
            model.increaseUnrest(1);
        } else if (modified[0] <= 12){
            println(model, "Unrest increases by two.");
            model.increaseUnrest(2);
        } else { // 13 or more
            println(model, "Unrest increases by three.");
            model.increaseUnrest(3);
        }
        return new StepToNextTurnState();
    }
}

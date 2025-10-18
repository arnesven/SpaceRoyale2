package model.states;

import model.Model;
import model.cards.alignment.AlignmentCard;
import model.cards.alignment.RebelAlignmentCard;
import model.cards.events.OppresiveBureaucracyEventCard;
import util.MyLists;
import util.MyRandom;

public class UnrestStepState extends GameState {
    @Override
    public GameState run(Model model) {
        int dieRoll = MyRandom.rollD10();
        int modified = dieRoll;
        println(model, "Unrest step - the die is rolled, it's a " + dieRoll + ".");
        String extra = "";
        AlignmentCard alignmentCard = model.drawBattleChanceCard();
        println(model, "Drawing Battle Chance card, it's " + alignmentCard.getName() + ".");
        if (alignmentCard instanceof RebelAlignmentCard) {
            extra += " +3";
            modified += 3;
        }
        if (MyLists.any(model.getEventCardsInPlay(),
                ev -> ev instanceof OppresiveBureaucracyEventCard)) {
            extra += " +1 (Oppressive Bureaucarcy)";
            modified += 1;
        }
        if (extra.isEmpty()) {
            print(model, "The result is " + dieRoll + ", ");
        } else {
            print(model, "The result is " + dieRoll + extra + " = " + modified + ", ");
        }

        if (modified <= 5) {
            println(model, "no effect.");
        } else if (modified <= 9) {
            println(model, "Unrest increases by one.");
            model.increaseUnrest(1);
        } else { // 10
            println(model, "Unrest increases by two.");
            model.increaseUnrest(2);
        }
        return new StepToNextTurnState();
    }
}

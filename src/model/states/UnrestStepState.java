package model.states;

import model.Model;
import util.MyRandom;

public class UnrestStepState extends GameState {
    @Override
    public GameState run(Model model) {
        println(model, "Unrest step - the die is rolled.");
        int dieRoll = MyRandom.rollD10();
        print(model, "The result is " + dieRoll + ", ");
        if (dieRoll <= 4) {
            println(model, "no effect.");
        } else if (dieRoll <= 9) {
            println(model, "Unrest increases by one.");
            model.increaseUnrest(1);
        } else { // 10
            println(model, "Unrest increases by two.");
            model.increaseUnrest(2);
        }
        return new StepToNextTurnState();
    }
}

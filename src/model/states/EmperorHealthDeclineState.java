package model.states;

import model.Model;
import util.MyRandom;

public class EmperorHealthDeclineState extends GameState {
    @Override
    public GameState run(Model model) {
        print(model, "Rolling die for Emperor Health Decline.");
        int dieRoll = MyRandom.rollD10();
        println(model, "... It's a " + dieRoll);
        print(model, "The Emperor's health is ");
        if (dieRoll < 3) {
            println(model, "unchanged.");
        } else if (dieRoll < 8) {
            println(model, "slowly declining.");
            model.advanceHealthCounter(1);
        } else if (dieRoll < 10) {
            println(model, "rapidly declining.");
            model.advanceHealthCounter(2);
        } else {
            println(model, "The Emperor's health declining very quickly.");
        }
        println(model, "Emperor Health: " + model.getEmperorHealth() + "/" + model.getEmperorMaxHealth());
        return new StepToNextTurnState();
    }
}

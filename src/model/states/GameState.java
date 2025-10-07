package model.states;

import model.Model;

import java.io.Serializable;

public abstract class GameState implements Serializable {
    public abstract GameState run(Model model);

    protected void println(Model model, String text) {
        model.getScreenHandler().println(text);
    }

    protected void print(Model model, String text) {
        model.getScreenHandler().print(text);
    }

    protected int integerInput(Model model, String prompt) {
        model.getScreenHandler().print(prompt + " ");
        return model.getScreenHandler().integerInput();
    }

    protected void waitForReturn(Model model) {
        model.getScreenHandler().lineInput();
    }

}

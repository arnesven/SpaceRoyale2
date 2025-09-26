package model.states;

import model.Model;

import java.util.Scanner;

public abstract class GameState {
    public abstract GameState run(Model model);

    protected void print(Model model, String text) {
        model.getScreenHandler().println(text);
    }

    protected int integerInput(Model model, String prompt) {
        model.getScreenHandler().print(prompt + " ");
        return model.getScreenHandler().integerInput();
    }

    protected void waitForReturn(Model model) {
        model.getScreenHandler().lineInput();
    }

}

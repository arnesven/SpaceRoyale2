package view;

import model.Model;
import model.Player;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoice {

    private List<String> options = new ArrayList<>();
    private List<MultipleChoiceAction> actions = new ArrayList<>();
    private int choice = -1;

    public void addOption(String optText, MultipleChoiceAction action) {
        this.options.add(optText);
        this.actions.add(action);
    }

    public void promptAndDoAction(Model model, String prompt, Player performer) {
        if (options.size() == 1) {
            choice = 1;
        } else {
            model.getScreenHandler().println(prompt);
            for (int i = 0; i < options.size(); ++i) {
                model.getScreenHandler().println("[" + (i + 1) + "] " + options.get(i));
            }
            do {
                this.choice = model.getScreenHandler().integerInput();
            } while (choice < 1 || choice > options.size());
        }
        executeOption(model, performer, choice);
    }

    public int noOfChoices() {
        return options.size();
    }

    public int getSelectedOptionIndex() {
        return choice;
    }

    public void removeSelectedOption() {
        options.remove(choice-1);
        actions.remove(choice-1);
    }

    public void executeOption(Model model, Player performer, int choice) {
        actions.get(choice-1).execute(model, performer);
    }
}

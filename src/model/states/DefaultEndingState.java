package model.states;

import model.Model;

public class DefaultEndingState extends GameState {
    @Override
    public GameState run(Model model) {
        model.setGameOver(true);
        return null;
    }
}

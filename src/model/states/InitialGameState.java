package model.states;

import model.Model;

public class InitialGameState extends GameState {
    @Override
    public GameState run(Model model) {
        int noOfPlayers = integerInput(model, "How many players will play?");
        return new SetUpGameState(noOfPlayers);
    }
}

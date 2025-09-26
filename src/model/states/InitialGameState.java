package model.states;

import model.Model;

public class InitialGameState extends GameState {
    @Override
    public GameState run(Model model) {
        println(model, "Welcome to Space Royal 2 Simulator");
        int noOfPlayers = integerInput(model, "How many players will play?");
        return new SetUpGameState(noOfPlayers);
    }
}

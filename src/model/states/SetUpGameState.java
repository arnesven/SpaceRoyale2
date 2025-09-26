package model.states;

import model.Model;

public class SetUpGameState extends GameState {
    private final int noOfPlayers;

    public SetUpGameState(int noOfPlayers) {
        this.noOfPlayers = noOfPlayers;
    }

    @Override
    public GameState run(Model model) {
        println(model, "Setting up the game.");
        println(model, "Setting up game boards.");
        model.setUpBoards();
        println(model, "Setting up players.");
        model.setUpPlayers(noOfPlayers);
        println(model, "Setting up tracks.");
        model.setUpTracks();
        println(model, "Setting up decks.");
        model.setUpDecks();
        println(model,"Setup complete. Press enter to continue.");
        waitForReturn(model);
        return new StartOfTurnState();
    }
}

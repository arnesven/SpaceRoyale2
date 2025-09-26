package model.states;

import model.Model;

public class SetUpGameState extends GameState {
    private final int noOfPlayers;

    public SetUpGameState(int noOfPlayers) {
        this.noOfPlayers = noOfPlayers;
    }

    @Override
    public GameState run(Model model) {
        print(model, "Setting up the game.");
        print(model, "Setting up game boards.");
        model.setUpBoards();
        print(model, "Setting up players.");
        model.setUpPlayers(noOfPlayers);
        print(model, "Setting up tracks.");
        model.setUpTracks();
        print(model, "Setting up decks.");
        model.setUpDecks();
        print(model,"Setup complete. Press enter to continue.");
        waitForReturn(model);
        return new StartOfTurnState();
    }
}

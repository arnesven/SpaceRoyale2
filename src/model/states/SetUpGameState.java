package model.states;

import model.Model;
import model.Player;
import model.cards.special.FirstTimeReorganizeSpecialEvent;

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
        Player player = model.getPlayers().get(model.getPlayers().size()-2);
        println(model, "Placing Special Event \"Reorganize\" after " + player.getName() + ".");
        model.getSpecialEvents().place(player, new FirstTimeReorganizeSpecialEvent());
        println(model,"Setup complete. Press enter to continue.");
        waitForReturn(model);
        return new StartOfTurnState();
    }
}

package model.states;

import model.GameTracks;
import model.Model;

public class StepToNextTurnState extends GameState {
    @Override
    public GameState run(Model model) {
        model.incrementTurn();
        if (model.getTurn() > GameTracks.MAX_GAME_TURN) {
            return new DefaultEndingState();
        }
        return new StartOfTurnState();
    }
}

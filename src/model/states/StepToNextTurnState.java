package model.states;

import model.GameTracks;
import model.Model;
import model.board.BattleAtTheRebelStronghold;
import model.board.BattleOfCentralia;

import java.util.ArrayList;

public class StepToNextTurnState extends GameState {
    @Override
    public GameState run(Model model) {
        model.incrementTurn();
        if (model.getTurn() > GameTracks.MAX_GAME_TURN) {
            return new DefaultEndingState();
        }
        if (model.checkForBattleOfCentralia()) {
            new BattleOfCentralia(model, new ArrayList<>()).resolveYourself(model);
        } else if (model.checkForBattleAtRebelStronghold()) {
            new BattleAtTheRebelStronghold(model, new ArrayList<>()).resolveYourself(model);
        }
        return new StartOfTurnState();
    }
}

package model.states;

import model.Model;

public class StartOfTurnState extends GameState {
    @Override
    public GameState run(Model model) {
        model.getScreenHandler().println("Turn " + model.getTurn());
        model.drawBoard();
        model.resetCollaborativeDraw();
        if (model.getTurn() == 1) {
            return new PlayerActionState(); // TODO: On turn 2 and forward, Trading Step
        }
        return new TradingStepState();
    }
}

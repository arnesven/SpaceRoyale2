package model.states;

import model.Model;

public class StartOfTurnState extends GameState {
    @Override
    public GameState run(Model model) {
        model.getScreenHandler().println("Turn " + model.getTurn());
        model.drawBoard();
        model.resetCollaborativeDraw();
        return new PlayerActionState();
    }
}

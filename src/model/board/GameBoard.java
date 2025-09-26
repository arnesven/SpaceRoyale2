package model.board;

import model.Model;

public class GameBoard {
    BoardLocation centralia = new CentraliaLocation();
    private final BattleBoard[] battles;

    public GameBoard() {
        this.battles = new BattleBoard[3];
        battles[0] = new InvasionBattleBoard('A');
        battles[1] = new DefendPlanetBattleBoard('B');
        battles[2] = new SpaceBattleBoard('C');
    }

    public BoardLocation getCentralia() {
        return centralia;
    }

    public void drawYourself(Model model) {
        for (int i = 0; i < battles.length; ++i) {
            battles[i].drawYourself(model, 2 + 20 * i, 2);
        }
        centralia.drawYourself(model, 2, 8);
    }

    public BattleBoard[] getBattles() {
        return battles;
    }

    public BattleBoard replaceBattle(Model model, BattleBoard bb) {
        for (int i = 0; i < battles.length; ++i) {
            if (battles[i] == bb) {
                BattleBoard newBattleBoard = bb.makeReplacement(model);
                model.getScreenHandler().println(battles[i].getName() + " is replaced by " + newBattleBoard.getName() + ".");
                battles[i] = newBattleBoard;
                return newBattleBoard;
            }
        }
        return null;
    }
}

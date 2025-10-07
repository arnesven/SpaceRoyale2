package model.board;

import model.Model;
import model.tracks.WarTrack;

import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;

public class GameBoard implements Serializable {
    private final BattleBoard[] battles;
    private final BoardLocation centralia = new CentraliaLocation();
    private final BoardLocation prisonPlanet = new PrisonPlanetLocation();
    private final BoardLocation rebelStronghold = new RebelStrongholdLocation();

    public GameBoard() {
        battles = new BattleBoard[3];
        List<BattleBoard> neutrals = WarTrack.getNeutralBattleBoards();
        for (int i = 0; i < battles.length; ++i) {
            battles[i] = neutrals.get(i);
        }
    }

    public BoardLocation getCentralia() {
        return centralia;
    }

    public void drawYourself(Model model, int xOff, int yOff) {
        for (int i = 0; i < battles.length; ++i) {
            battles[i].drawYourself(model, xOff + 20 * i, yOff);
        }
        centralia.drawYourself(model, xOff, yOff + 6);
        prisonPlanet.drawYourself(model, xOff, yOff + 9);
        rebelStronghold.drawYourself(model, xOff + 49, yOff+5);
    }

    public BattleBoard[] getBattles() {
        return battles;
    }

    public BattleBoard replaceBattle(Model model, BattleBoard bb) {
        for (int i = 0; i < battles.length; ++i) {
            if (battles[i] == bb) {
                BattleBoard newBattleBoard = model.makeBattleBoardReplacement(model, bb);
                battles[i] = newBattleBoard;
                return newBattleBoard;
            }
        }
        throw new NoSuchElementException("The battle just resolved can not be replaced, " +
                "because it is not currently on the board!");
    }

    public BoardLocation getPrisonPlanet() {
        return prisonPlanet;
    }

    public BoardLocation getRebelStronghold() {
        return rebelStronghold;
    }
}

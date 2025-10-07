package model.tracks;

import model.board.BattleBoard;
import model.board.DefendPlanetBattleBoard;
import model.board.InvasionBattleBoard;
import model.board.SpaceBattleBoard;

import java.io.Serializable;
import java.util.List;

public class WarTrack implements Serializable {

    /**
     *
     * TURN 1-3:
     *       | BoC | -4 | -3 | -2 | -1 |  0  |  1 |  2 |  3 |  4 |  BatRS  |
     *            Rebel Advantage <               > Empire Advantage
     *
     * TURN 4-6:
     *            | BoC | -3 | -2 | -1 |  0  |  1 |  2 |  3 |  BatRS  |
     *                 Rebel Advantage <     > Empire Advantage
     *
     * TURN 7-8:
     *                 | BoC | -2 | -1 |  0  |  1 |  2 |  BatRS  |
     *                 Rebel Advantage <     > Empire Advantage
     *
     */

    private static final List<BattleBoard> NEUTRAL_BOARDS = List.of(
            new InvasionBattleBoard('A'),
            new DefendPlanetBattleBoard('B'),
            new SpaceBattleBoard('C'));

    private static final BattleBoard EMPIRE_ADVANTAGE_BOARD = new InvasionBattleBoard('D');
    private static final BattleBoard REBEL_ADVANTAGE_BOARD = new DefendPlanetBattleBoard('E');

    private int turn = 1;
    private int counterPosition = 0;

    public static List<BattleBoard> getNeutralBattleBoards() {
        return NEUTRAL_BOARDS;
    }

    public int getCounter() {
        return counterPosition;
    }

    public void setCounter(int i) {
        counterPosition = i;
    }

    public void moveCounter(int i) {
        counterPosition += i;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public boolean isBattleOfCentralia(int turn) {
        return counterPosition <= -1 * getSpecialBattleSpace(turn);
    }

    public boolean isBattleAtRebelStronghold(int turn) {
        return counterPosition >= getSpecialBattleSpace(turn);
    }

    public BattleBoard replaceBoard(BattleBoard bb) {
        // Find all cases of obsolete boards.
        if (isRebelAdvantage()) {
            if (bb.getIdentifier() == NEUTRAL_BOARDS.getFirst().getIdentifier() ||
                    bb.getIdentifier() == EMPIRE_ADVANTAGE_BOARD.getIdentifier()) {
                return REBEL_ADVANTAGE_BOARD;
            }
        } else if (isEmpireAdvantage()) {
            if (bb.getIdentifier() == NEUTRAL_BOARDS.getLast().getIdentifier() ||
                    bb.getIdentifier() == REBEL_ADVANTAGE_BOARD.getIdentifier()) {
                return EMPIRE_ADVANTAGE_BOARD;
            }
        } else { // Neither has advantage
            if (bb.getIdentifier() == REBEL_ADVANTAGE_BOARD.getIdentifier()) {
                return NEUTRAL_BOARDS.getFirst();
            }
            if (bb.getIdentifier() == EMPIRE_ADVANTAGE_BOARD.getIdentifier()) {
                return NEUTRAL_BOARDS.getLast();
            }
        }

        // Board is not obsolete.
        return bb.flipBattleBoard();
    }

    private int getSpecialBattleSpace(int turn) {
        if (turn < 4) {
            return 5;
        }
        if (turn < 7) {
            return 4;
        }
        return 3;
    }

    private boolean isEmpireAdvantage() {
        return counterPosition >= getEmpireAdvantageThreshold(turn);
    }

    private boolean isRebelAdvantage() {
        return counterPosition <= getRebelAdvantageThreshold(turn);
    }

    private int getEmpireAdvantageThreshold(int turn) {
        return -1 * getRebelAdvantageThreshold(turn);
    }

    private int getRebelAdvantageThreshold(int turn) {
        if (turn < 4) {
            return -2;
        }
        return -1;
    }

    public String asString() {
        String extra = "";
        if (isEmpireAdvantage()) {
            extra = " (Empire Advantage)";
        } else if (isRebelAdvantage()) {
            extra = " (Rebel Advantage)";
        }
        return counterPosition + extra;
    }
}

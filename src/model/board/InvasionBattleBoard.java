package model.board;

import model.Model;
import model.Player;

import java.util.List;

public class InvasionBattleBoard extends BattleBoard {

    public InvasionBattleBoard(char identifier) {
        super("Invasion", identifier);
    }

    @Override
    protected boolean battleSpecificResolve(Model model, List<Player> playersInBattle, boolean empireWinsSpace, boolean empireWinsGround) {
        if (empireWinsSpace && empireWinsGround) {
            model.getScreenHandler().println("The empire has successfully invaded the planet.");
            for (Player p : playersInBattle) {
                model.getScreenHandler().println(p.getName() + " gains 2 Emperor Influence.");
                p.addToEmperorInfluence(2);
            }
            return true;
        }
        model.getScreenHandler().println("The rebels have thwarted the imperial invasion.");
        return false;
    }

    @Override
    public BattleBoard makeReplacement(Model model) {
        if (getIdentifier() == 'A' || getIdentifier() == 'D') {
            return new SpaceBattleBoard(getIdentifier());
        }
        return new DefendPlanetBattleBoard(getIdentifier());
    }
}

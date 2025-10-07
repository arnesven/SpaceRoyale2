package model.board;

import model.Model;
import model.Player;

import java.util.List;

public class InvasionBattleBoard extends BattleBoard {

    public InvasionBattleBoard(String name, char identifier) {
        super(name, identifier);
    }

    public InvasionBattleBoard(char identifier) {
        this("Invasion", identifier);
    }

    @Override
    protected boolean battleSpecificResolve(Model model, List<Player> playersInBattle, boolean empireWinsSpace, boolean empireWinsGround) {
        if (empireWinsSpace && empireWinsGround) {
            model.getScreenHandler().println("The empire has successfully invaded the planet.");
            return true;
        }
        if (empireWinsSpace) {
            model.getScreenHandler().println("The Empire establish a blockade of the planet,");
            model.getScreenHandler().println("but the rebel forces hold out until a relief force can break the siege.");
        } else if (empireWinsGround) {
            model.getScreenHandler().println("The Empire quickly lands troops and overruns the planet.");
            model.getScreenHandler().println("However, their supply lines are soon cut off by the rebel fleet which dominates the skies.");
        }
        model.getScreenHandler().println("The rebels have thwarted the imperial invasion.");
        return false;
    }

    @Override
    protected void battleSpecificRewards(Model model, List<Player> playersInBattle, boolean imperialWin) {
        if (imperialWin) {
            for (Player p : playersInBattle) {
                model.getScreenHandler().println(p.getName() + " gains 2 Emperor Influence.");
                p.addToEmperorInfluence(2);
            }
        }
    }

    @Override
    public BattleBoard flipBattleBoard() {
        if (getIdentifier() == 'A' || getIdentifier() == 'D') {
            return new SpaceBattleBoard(getIdentifier());
        }
        return new DefendPlanetBattleBoard(getIdentifier());
    }
}

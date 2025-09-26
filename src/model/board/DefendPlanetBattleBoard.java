package model.board;

import model.Model;
import model.Player;

import java.util.List;

public class DefendPlanetBattleBoard extends BattleBoard {
    public DefendPlanetBattleBoard(char identifier) {
        super("Defend Planet", identifier);
    }

    @Override
    protected boolean battleSpecificResolve(Model model, List<Player> playersInBattle, boolean empireWinsSpace, boolean empireWinsGroundDomain) {
        if (empireWinsSpace || empireWinsGroundDomain) {
            model.getScreenHandler().println("The Imperial forces have successfully defended the planet.");
            for (Player p : playersInBattle) {
                model.getScreenHandler().println(p.getName() + " gains 1 Popular Influence.");
                p.addToPopularInfluence(+1);
                // TODO: Let player draw 1 unit or 1 tactics
            }
            return true;
        }
        model.getScreenHandler().println("The planet has fallen to the rebels.");
        return false;
    }

    @Override
    public BattleBoard makeReplacement(Model model) {
        if (getIdentifier() == 'C' || getIdentifier() == 'E') {
            return new SpaceBattleBoard(getIdentifier());
        }
        return new InvasionBattleBoard(getIdentifier());
    }
}

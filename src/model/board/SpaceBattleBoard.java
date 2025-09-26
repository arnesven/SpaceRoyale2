package model.board;

import model.Model;
import model.Player;
import model.cards.EmpireUnitCard;
import model.cards.RebelUnitCard;

import java.util.List;

public class SpaceBattleBoard extends BattleBoard {
    public SpaceBattleBoard(char identifier) {
        super("Space Battle", identifier);
    }

    @Override
    protected boolean resolveGroundDomain(Model model, List<RebelUnitCard> rebelUnits, List<EmpireUnitCard> empireUnits) {
        return true;
    }

    @Override
    protected boolean battleSpecificResolve(Model model, List<Player> playersInBattle, boolean empireWinsSpace, boolean empireWinsGroundDomain) {
        if (empireWinsSpace) {
            model.getScreenHandler().println("The Empire is victorious in the space battle.");
            for (Player p : playersInBattle) {
                model.getScreenHandler().println(p.getName() + " gains 1 Emperor Influence.");
                p.addToEmperorInfluence(1);
            }
            return true;
        }
        model.getScreenHandler().println("The rebels are victorious in the space battle.");
        return false;
    }

    @Override
    public BattleBoard makeReplacement(Model model) {
        if (getIdentifier() == 'A' || getIdentifier() == 'D') {
            return new InvasionBattleBoard(getIdentifier());
        }
        return new DefendPlanetBattleBoard(getIdentifier());
    }
}

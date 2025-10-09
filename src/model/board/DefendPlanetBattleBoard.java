package model.board;

import model.Model;
import model.Player;
import view.MultipleChoice;

import java.util.List;

public class DefendPlanetBattleBoard extends BattleBoard {
    public DefendPlanetBattleBoard(String name, char identifier) {
        super(name, identifier);
    }

    public DefendPlanetBattleBoard(char identifier) {
        this("Defend Planet", identifier);
    }

    @Override
    protected boolean minesAreEffective(Model model) {
        return false;
    }

    @Override
    protected boolean battleSpecificResolve(Model model, List<Player> playersInBattle, boolean empireWinsSpace, boolean empireWinsGroundDomain) {
        if (empireWinsSpace || empireWinsGroundDomain) {
            if (!empireWinsSpace) {
                model.getScreenHandler().println("The Rebel establish a blockade of the planet,");
                model.getScreenHandler().println("but the imperial forces hold out until a relief force can break the siege.");
            } else if (!empireWinsGroundDomain) {
                model.getScreenHandler().println("The Rebels quickly land troops and overrun the planet.");
                model.getScreenHandler().println("However, their supply lines are soon cut off by the imperial fleet which dominates the skies.");
            }
            model.getScreenHandler().println("The Imperial forces have successfully defended the planet.");
            return true;
        }
        model.getScreenHandler().println("The planet has fallen to the rebels.");
        return false;
    }

    @Override
    protected void battleSpecificRewards(Model model, List<Player> playersInBattle, boolean imperialWin) {
        if (imperialWin) {
            for (Player p : playersInBattle) {
                model.getScreenHandler().println(p.getName() + " gains 1 Popular Influence.");
                p.addToPopularInfluence(+1);
                MultipleChoice multipleChoice = new MultipleChoice();
                multipleChoice.addOption("Draw Unit Card", (m, _) -> p.drawUnitCard(m));
                if (model.isTacticsCardsAvailable()) {
                    multipleChoice.addOption("Draw Tactics Card", (m, _) -> p.drawTacticsCard(m));
                }
                multipleChoice.promptAndDoAction(model, p.getName() + " gets one card as a reward:", p);
                model.getScreenHandler().println(p.getName() + "'s hand:");
                p.printHand(model.getScreenHandler());
            }
        }
    }

    @Override
    public BattleBoard flipBattleBoard() {
        if (getIdentifier() == 'C' || getIdentifier() == 'E') {
            return new SpaceBattleBoard(getIdentifier());
        }
        return new InvasionBattleBoard(getIdentifier());
    }
}

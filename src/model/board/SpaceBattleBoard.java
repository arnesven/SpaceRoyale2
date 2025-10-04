package model.board;

import model.Model;
import model.Player;
import model.cards.alignment.AlignmentCard;
import model.cards.units.EmpireUnitCard;
import model.cards.units.RebelUnitCard;
import model.cards.units.UnitCard;
import util.MyLists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpaceBattleBoard extends BattleBoard {
    public SpaceBattleBoard(char identifier) {
        super("Space Battle", identifier);
    }

    @Override
    protected boolean resolveGroundDomain(Model model, List<RebelUnitCard> rebelUnits, List<EmpireUnitCard> empireUnits, AlignmentCard battleBonus) {
        return true;
    }

    @Override
    protected int getGroundBonus(AlignmentCard battleChance) {
        return 0;
    }

    @Override
    protected int getSpaceBonus(AlignmentCard battleChance) {
        return 2 * super.getSpaceBonus(battleChance);
    }

    @Override
    protected boolean battleSpecificResolve(Model model, List<Player> playersInBattle, boolean empireWinsSpace, boolean empireWinsGroundDomain) {
        if (empireWinsSpace) {
            model.getScreenHandler().println("The Empire won the battle.");
            for (Player p : playersInBattle) {
                model.getScreenHandler().println(p.getName() + " gains 1 Emperor Influence.");
                p.addToEmperorInfluence(1);
            }
            return true;
        }
        model.getScreenHandler().println("The rebels won the battle.");
        return false;
    }

    @Override
    protected void discardRebelCards(Model model, List<RebelUnitCard> rebelUnitCards, boolean imperialWin) {
        List<BattleBoard> otherBattles = new ArrayList<>();
        for (BattleBoard bb : model.getBattles()) {
            if ((imperialWin && bb instanceof InvasionBattleBoard) ||
                    (!imperialWin && bb instanceof DefendPlanetBattleBoard)) {
                otherBattles.add(bb);
            }
        }
        otherBattles.remove(this);
        List<RebelUnitCard> groundUnits = sendUnitsToSpecialBattle(model, rebelUnitCards);
        if (otherBattles.isEmpty()) {
            model.getScreenHandler().println("Since there is no suitable battle for the rebel Ground units to go, they are discarded.");
            model.discardRebelCards(groundUnits);
            return;
        }
        otherBattles.sort(Comparator.comparingInt(BattleBoard::getIdentifier));
        BattleBoard destination = otherBattles.getFirst();
        model.getScreenHandler().println("The ground units from the space battle are moved to " + destination.getName());
        for (RebelUnitCard ru : groundUnits) {
            destination.addRebelCard(ru);
        }
    }

    @Override
    protected List<RebelUnitCard> sendUnitsToSpecialBattle(Model model, List<RebelUnitCard> rebelUnits) {
        model.discardRebelCards(MyLists.filter(rebelUnits, rebelUnitCard -> !rebelUnitCard.isGroundUnit()));
        return MyLists.filter(rebelUnits, UnitCard::isGroundUnit);
    }

    @Override
    public BattleBoard flipBattleBoard() {
        if (getIdentifier() == 'A' || getIdentifier() == 'D') {
            return new InvasionBattleBoard(getIdentifier());
        }
        return new DefendPlanetBattleBoard(getIdentifier());
    }

    @Override
    public boolean canUseBombardment() {
        return false;
    }
}

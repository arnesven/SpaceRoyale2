package model.board;

import model.Model;
import model.Player;
import model.cards.EmpireUnitCard;
import model.cards.RebelUnitCard;
import model.cards.UnitCard;
import util.MyLists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

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
    protected void endOfBattle(Model model, List<RebelUnitCard> rebelUnitCards, List<EmpireUnitCard> empireUnitCards, boolean imperialWin) {
        // TODO: Only if special battle did not occur.
        List<BattleBoard> otherBattles = new ArrayList<>();
        for (BattleBoard bb : model.getBattles()) {
            if ((imperialWin && bb instanceof InvasionBattleBoard) ||
                    (!imperialWin && bb instanceof DefendPlanetBattleBoard)) {
                otherBattles.add(bb);
            }
        }
        model.discardRebelCards(MyLists.filter(rebelUnitCards, rebelUnitCard -> !rebelUnitCard.isGroundUnit()));
        model.discardEmpireCards(empireUnitCards);
        if (otherBattles.isEmpty()) {
            return;
        }
        otherBattles.sort(Comparator.comparingInt(BattleBoard::getIdentifier));
        BattleBoard destination = otherBattles.getFirst();
        model.getScreenHandler().println("The ground units from the space battle are moved to " + destination.getName());
        List<RebelUnitCard> groundUnits = MyLists.filter(rebelUnitCards, UnitCard::isGroundUnit);
        for (RebelUnitCard ru : groundUnits) {
            destination.addRebelCard(ru);
        }
        if (destination.battleIsTriggered()) {
            model.resolveBattle(destination);
        }
    }

    @Override
    public BattleBoard makeReplacement(Model model) {
        if (getIdentifier() == 'A' || getIdentifier() == 'D') {
            return new InvasionBattleBoard(getIdentifier());
        }
        return new DefendPlanetBattleBoard(getIdentifier());
    }
}

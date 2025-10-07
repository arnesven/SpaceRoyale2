package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.board.DefendPlanetBattleBoard;
import model.cards.GameCard;
import model.cards.units.*;
import util.MyLists;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public class BombardmentCard extends TacticsCard {
    public BombardmentCard() {
        super("Bombardment");
    }

    @Override
    public boolean playedAfterReveal() {
        return true;
    }

    @Override
    public void resolve(Model model, Player player, BattleBoard battle) {
        if (!battle.canUseBombardment()) {
            model.getScreenHandler().println("Bombardment has no effect in Space battles.");
            return;
        }
        List<UnitCard> cardsToDiscard = new ArrayList<>();
        MultipleChoice multipleChoice = new MultipleChoice();
        for (RebelUnitCard ru : MyLists.filter(battle.getRebelUnits(), this::isDestroyableGroundUnit)) {
            multipleChoice.addOption(ru.getName(), (m, p) -> {cardsToDiscard.add(ru);});
        }
        for (EmpireUnitCard eu : MyLists.filter(battle.getEmpireUnits(), UnitCard::isGroundUnit)) {
            multipleChoice.addOption(eu.getName() + " (Empire)", (m, p) -> cardsToDiscard.add(eu));
        }
        while (cardsToDiscard.size() < 3) {
            multipleChoice.promptAndDoAction(model, "Select a ground unit to discard:", player);
            multipleChoice.removeSelectedOption();
        }

        for (UnitCard uc : cardsToDiscard) {
            model.getScreenHandler().println("Discarding " + uc.getName() + ".");
            if (uc instanceof RebelUnitCard) {
                battle.removeUnit((RebelUnitCard) uc);
                model.discardRebelCards(List.of((RebelUnitCard)uc));
            } else {
                battle.removeUnit((EmpireUnitCard) uc);
                model.discardEmpireCards(List.of((EmpireUnitCard)uc));
            }
        }
        player.addToPopularInfluence(-1);
        model.getScreenHandler().println(player.getName() + " gets -1 Popular Influence.");
        if (battle instanceof DefendPlanetBattleBoard) {
            player.addToPopularInfluence(-1);
            model.getScreenHandler().println("Since this was a Defend Planet battle, " +
                    player.getName() + " gets an additional -1 Popular Influence.");
            player.addToPopularInfluence(-1);
        }
    }

    private boolean isDestroyableGroundUnit(RebelUnitCard rebelUnitCard) {
        if (!rebelUnitCard.isGroundUnit()) {
            return false;
        }
        if (rebelUnitCard instanceof UpgradeableRebelUnitCard) {
            return !((UpgradeableRebelUnitCard) rebelUnitCard).isUpgraded();
        }
        if (rebelUnitCard instanceof RebelCommandosUnitCard) {
            return false;
        }
        return true;
    }

    @Override
    public GameCard copy() {
        return new BombardmentCard();
    }
}

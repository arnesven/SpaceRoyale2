package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.GameCard;
import model.cards.units.RebelUnitCard;
import model.cards.units.UnitCard;
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
        List<RebelUnitCard> cardsToDiscard = new ArrayList<>();
        List<RebelUnitCard> ground = MyLists.filter(battle.getRebelUnits(), UnitCard::isGroundUnit);
        if (ground.size() <= 3) {
            cardsToDiscard.addAll(ground);
        } else {
            MultipleChoice multipleChoice = new MultipleChoice();
            for (RebelUnitCard ru : ground) {
                multipleChoice.addOption(ru.getName(), (m, p) -> {cardsToDiscard.add(ru);});
            }
            while (cardsToDiscard.size() < 3) {
                multipleChoice.promptAndDoAction(model, "Select a ground unit to discard:", player);
                multipleChoice.removeSelectedOption();
            }
        }
        for (RebelUnitCard ru : cardsToDiscard) {
            model.getScreenHandler().println("Discarding " + ru.getName() + ".");
            battle.removeUnit(ru);
        }
        model.discardRebelCards(cardsToDiscard);
        player.addToPopularInfluence(-1);
        model.getScreenHandler().println(player.getName() + " gets -1 Popular Influence.");
    }

    @Override
    public GameCard copy() {
        return new BombardmentCard();
    }
}

package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.GameCard;
import model.cards.units.EmpireUnitCard;
import view.MultipleChoice;

public class ReinforceCard extends TacticsCard {
    public ReinforceCard() {
        super("Reinforce");
    }

    @Override
    public boolean canBePlayedOutsideOfBattle() {
        return true;
    }

    @Override
    public boolean playedAfterReveal() {
        return true;
    }

    @Override
    public void resolve(Model model, Player player, BattleBoard battle) {
        if (player.getCurrentLocation() != battle) {
            model.getScreenHandler().println(player.getName() + " moves to " + battle.getName() + ".");
            player.moveToLocation(battle);
        }
        if (player.getUnitCardsInHand().isEmpty()) {
            model.getScreenHandler().println(player.getName() + " has no cards to add to the battle.");
            return;
        }
        MultipleChoice multipleChoice = new MultipleChoice();
        for (EmpireUnitCard eu : player.getUnitCardsInHand()) {
            multipleChoice.addOption(eu.getNameAndStrength(), (_, performer) -> {
                battle.addEmpireUnit(eu);
                performer.removeUnitCardFromHand(eu);
                model.getScreenHandler().println(player.getName() + " added " + eu.getName() + " to the battle.");
            });
        }
        multipleChoice.promptAndDoAction(model, "Which card do you wish " + player.getName() + " to add to the battle?", player);
    }

    @Override
    public GameCard copy() {
        return new ReinforceCard();
    }
}

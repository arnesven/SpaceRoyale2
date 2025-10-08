package model.cards.tactics;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.GameCard;

public class RapidMobilizationCard extends TacticsCard {
    public RapidMobilizationCard() {
        super("Rapid Mobilization");
    }

    @Override
    public boolean playedAfterReveal() {
        return false;
    }

    @Override
    public boolean playAfterBattle() {
        return false;
    }

    @Override
    public void resolve(Model model, Player player, BattleBoard battle) {
        model.getScreenHandler().println(player.getName() + " plays " + getName() + " to force " +
                battle.getName() + " to be resolved.");
        model.resolveBattle(battle);
    }

    @Override
    public GameCard copy() {
        return new RapidMobilizationCard();
    }
}

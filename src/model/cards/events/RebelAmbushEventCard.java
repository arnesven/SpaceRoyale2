package model.cards.events;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.DeckIsEmptyException;
import model.cards.GameCard;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public class RebelAmbushEventCard extends EventCard {
    public RebelAmbushEventCard() {
        super("Rebel Ambush", true,
                "Choose a battle which has at least as many Rebel Unit " +
                        "cards as Empire Unit cards. Add 2 Rebel Units to that battle from the deck, " +
                        "then resolve that battle.");
    }

    @Override
    public void resolve(Model model, Player player) {
        List<BattleBoard> suitableBattles = new ArrayList<>();
        for (BattleBoard bb : model.getBattles()) {
            if (bb.getRebelUnits().size() >= bb.getEmpireUnits().size()) {
                suitableBattles.add(bb);
            }
        }
        if (suitableBattles.isEmpty()) {
            model.getScreenHandler().println("No battles fulfill the criteria, event has no effect.");
            return;
        }

        MultipleChoice multipleChoice = new MultipleChoice();
        for (BattleBoard bb : suitableBattles) {
            multipleChoice.addOption(bb.getName(), (m, p) -> {
                m.getScreenHandler().println("Adding 2 Rebel Units to " + bb.getName() + ".");
                try {
                    bb.addRebelCard(model.drawRebelUnitCard());
                    bb.addRebelCard(model.drawRebelUnitCard());
                } catch (DeckIsEmptyException die) {
                    model.getScreenHandler().println("Rebel Unit deck is empty, added all available.");
                }
                m.resolveBattle(bb);
            });
        }
        multipleChoice.promptAndDoAction(model, "Which battle do you want to resolve?", player);
    }

    @Override
    public GameCard copy() {
        return new RebelAmbushEventCard();
    }
}

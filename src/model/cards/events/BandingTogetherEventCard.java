package model.cards.events;

import model.Model;
import model.Player;
import model.board.BattleBoard;
import model.cards.DeckIsEmptyException;
import model.cards.GameCard;
import util.MyStrings;
import view.MultipleChoice;

public class BandingTogetherEventCard extends EventCard {
    private static final int UNITS_TO_ADD = 3;

    public BandingTogetherEventCard() {
        super("Banding Together", false,
                "Draw " + UNITS_TO_ADD + " Rebel Unit Cards and add them to a single Battle without looking at them.");
    }

    @Override
    public void resolve(Model model, Player player) {
        MultipleChoice multipleChoice = new MultipleChoice();
        for (BattleBoard bb : model.getBattles()) {
            multipleChoice.addOption(bb.getName(), (m, p) -> {
                model.getScreenHandler().println(MyStrings.capitalize(MyStrings.numberWord(UNITS_TO_ADD)) +
                        " Rebel Unit cards added to " + bb.getName() + ".");
                for (int i = 0; i < UNITS_TO_ADD; ++i) {
                    try {
                        bb.addRebelCard(model.drawRebelUnitCard());
                    } catch (DeckIsEmptyException die) {
                        model.getScreenHandler().println("Rebel Unit deck is empty, added " + i + " cards.");
                        break;
                    }
                }
            });
        }
        multipleChoice.promptAndDoAction(model, "Which battle do you add cards to?", player);
    }

    @Override
    public GameCard copy() {
        return new BandingTogetherEventCard();
    }
}

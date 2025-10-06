package model.cards.events;

import model.Model;
import model.Player;
import model.cards.units.EmpireUnitCard;
import model.cards.GameCard;
import view.MultipleChoice;

public class DisastrousMoraleEventCard extends EventCard {
    public DisastrousMoraleEventCard() {
        super("Disastrous Morale", false,
                "Each player discards two Unit cards from their hand.");
    }

    @Override
    public void resolve(Model model, Player player) {
        for (Player p : model.getPlayersNotDefectors()) {
            MultipleChoice multipleChoice = new MultipleChoice();
            for (EmpireUnitCard eu : p.getUnitCardsInHand()) {
                multipleChoice.addOption(eu.getNameAndStrength(), (m, performer) -> performer.discardCard(m, eu));
            }
            for (int i = 0; i < 2 && !p.getUnitCardsInHand().isEmpty(); ++i) {
                multipleChoice.promptAndDoAction(model, "Which card does " + p.getName() + " discard?", p);
                multipleChoice.removeSelectedOption();
            }
        }
    }

    @Override
    public GameCard copy() {
        return new DisastrousMoraleEventCard();
    }
}

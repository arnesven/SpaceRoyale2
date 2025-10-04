package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.units.EmpireUnitCard;
import view.MultipleChoice;

public class PropagandaCampaignEventCard extends EventCard {
    public PropagandaCampaignEventCard() {
        super("Propaganda Campaign", true,
                "The current player may discard 1 Unit card to gain 1 Popular Influence.");
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println(player.getName() + " may discard 1 Unit card to gain 1 Popular Influence.");
        MultipleChoice multipleChoice = new MultipleChoice();
        for (EmpireUnitCard eu : player.getUnitCardsInHand()) {
            multipleChoice.addOption(eu.getName(), (m, p) -> {
                p.discardCard(m, eu);
                m.getScreenHandler().println(p.getName() + " discards " + eu.getName() + ", gains 1 Popular Influence.");
                p.addToPopularInfluence(1);
            });
        }
        multipleChoice.addOption("Pass", (m, p) -> {
            m.getScreenHandler().println(p.getName() + " opts to not discard a Unit card.");
        });
        multipleChoice.promptAndDoAction(model, "Select a card to discard.", player);
    }

    @Override
    public GameCard copy() {
        return new PropagandaCampaignEventCard();
    }
}

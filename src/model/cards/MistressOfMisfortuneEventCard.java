package model.cards;

import model.Model;
import model.Player;

public class MistressOfMisfortuneEventCard extends EventCard {
    public MistressOfMisfortuneEventCard() {
        super("Mistress of Misfortune", false, "Add 1 Rebel Alignment card to the Battle Chance deck.");
    }

    @Override
    public void resolve(Model model, Player player) {
        model.getScreenHandler().println("Adding 1 Rebel Alignment card to the Battle Chance deck.");
        model.addBattleChanceCard(new RebelAlignmentCard());
    }

    @Override
    public GameCard copy() {
        return new MistressOfMisfortuneEventCard();
    }
}

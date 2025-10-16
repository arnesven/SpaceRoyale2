package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.units.EmpireBlackCrusaderUnitCard;
import model.cards.units.EmpireUnitCard;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public class BlackCrusaderEventCard extends EventCard {
    private final int INFLUENCE_NEEDED = 2;

    private List<EmpireUnitCard> cards = new ArrayList<>();

    public BlackCrusaderEventCard() {
        super("Black Crusader", true,
                "If you have at least 3 Emperor Influence, secretly look at the " +
                        "Black Crusader Empire Unit cards and add one of them to your hand.");
        cards.add(new EmpireBlackCrusaderUnitCard(false, false));
        cards.add(new EmpireBlackCrusaderUnitCard(true, false));
        cards.add(new EmpireBlackCrusaderUnitCard(false, true));
        cards.add(new EmpireBlackCrusaderUnitCard(true, true));
    }

    @Override
    public void resolve(Model model, Player player) {
        if (player.getEmperorInfluence() >= INFLUENCE_NEEDED) {
            if (cards.isEmpty()) {
                model.getScreenHandler().println("All Black Crusader cards have been taken already. Event has no effect.");
            } else {
                MultipleChoice multipleChoice = new MultipleChoice();
                for (EmpireUnitCard eu : cards) {
                    multipleChoice.addOption(eu.getName(), (m, p) -> {
                        cards.remove(eu);
                        player.addCardToHand(eu);
                        model.getScreenHandler().println(player.getName() + " picks up " + eu.getName() + ".");
                    });
                }
                multipleChoice.promptAndDoAction(model, "Which Black Crusader card do you want?", player);
            }
        } else {
            model.getScreenHandler().println("Player does not have enough influence to get a Black Crusader card.");
        }
    }

    @Override
    public GameCard copy() {
        return new BlackCrusaderEventCard();
    }
}

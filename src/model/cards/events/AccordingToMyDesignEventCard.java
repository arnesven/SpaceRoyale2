package model.cards.events;

import model.Model;
import model.Player;
import model.cards.GameCard;
import model.cards.tactics.TacticsCard;
import util.Arithmetics;
import util.MyLists;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public class AccordingToMyDesignEventCard extends EventCard {
    private static final int NO_OF_CARDS = 3;

    public AccordingToMyDesignEventCard() {
        super("According to My Design", false,
                "If players collectively discard " + NO_OF_CARDS +
                        " Tactics cards, move the War counter two steps closer to the 'Battle at Rebel Stronghold' space");
    }

    @Override
    public void resolve(Model model, Player player) {
        if (MyLists.intAccumulate(model.getPlayers(), p -> p.getTacticsCardsInHand().size()) < NO_OF_CARDS) {
            model.getScreenHandler().println("Not enough Tactics cards in hands of players. Event has no effect.");
            return;
        }

        List<TacticsCard> discardedCards = new ArrayList<>();
        int passes = 0;
        List<Player> players = MyLists.filter(model.getPlayersStartingFrom(player),
                p -> !p.getTacticsCardsInHand().isEmpty());
        int index = 0;
        do {
            Player current = players.get(index);
            MultipleChoice multipleChoice = new MultipleChoice();
            for (TacticsCard tc : current.getTacticsCardsInHand()) {
                multipleChoice.addOption(tc.getName(), (m, p) -> {
                    p.discardCard(m, tc);
                    discardedCards.add(tc);
                    m.getScreenHandler().println(p.getName() + " discards " + tc.getName() + ".");
                });
            }
            multipleChoice.addOption("Pass", (m,p) -> {
                m.getScreenHandler().println(p.getName() + " declines to discard a Tactics card.");
            });
            multipleChoice.promptAndDoAction(model, "Select a Tactics card from " + current.getName() + "'s hand to discard, or pass.", current);
            if (multipleChoice.getSelectedOptionIndex() == multipleChoice.getNumberOfChoices()) {
                passes++;
            } else {
                passes = 0;
            }
            index = Arithmetics.incrementWithWrap(index, players.size());
        } while (discardedCards.size() < NO_OF_CARDS && passes < model.getPlayers().size());

        if (discardedCards.size() == NO_OF_CARDS) {
            model.getScreenHandler().println(NO_OF_CARDS + " Tactics cards discarded. The War counter advances twice.");
            model.advanceWarCounter();
            model.advanceWarCounter();
        } else {
            model.getScreenHandler().println("All players have passed.");
            model.getScreenHandler().println("Not enough Tactics cards discarded. Event has no effect.");
        }


    }

    @Override
    public GameCard copy() {
        return new AccordingToMyDesignEventCard();
    }
}

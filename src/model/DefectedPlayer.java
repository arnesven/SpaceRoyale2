package model;

import model.board.BattleAtTheRebelStronghold;
import model.board.BattleBoard;
import model.cards.alignment.NoAlignmentCard;
import model.cards.events.EventCard;
import model.cards.units.*;
import model.states.PlayerActionState;
import util.MyLists;
import view.MultipleChoice;

import view.ScreenHandler;

import java.util.ArrayList;
import java.util.List;

public class DefectedPlayer extends Player {
    private final Player inner;
    private final List<RebelUnitCard> cardsInHand = new ArrayList<>();
    private EventCard heldEvent = null;

    public DefectedPlayer(Player player) {
        super(player.getColor(), player.getName(), player.getShortName().charAt(0));
        this.inner = player;
        setLoyaltyCard(new NoAlignmentCard());
    }

    public void addCardToHand(RebelUnitCard ru) {
        cardsInHand.add(ru);
    }

    public void discardCard(Model model, RebelUnitCard ru) {
        cardsInHand.remove(ru);
        model.discardRebelCards(List.of(ru));
    }

    @Override
    public String getName() {
        return inner.getName() + "(Defector)";
    }

    public void tradeInUnits(Model model, Player player) {
        if (MyLists.any(player.getUnitCardsInHand(), eu -> eu instanceof AgentUnitCard)) {
            RebelOperativeUnitCard operative = new RebelOperativeUnitCard();
            model.getScreenHandler().println(player.getName() + " trades in one Agent card for an " + operative.getName() + " card.");
            addCardToHand(operative);
        }
        EmpireUnitCard groundUnit = MyLists.find(player.getUnitCardsInHand(), UnitCard::isGroundUnit);
        if (groundUnit != null) {
            RebelCommandosUnitCard commandos = new RebelCommandosUnitCard();
            model.getScreenHandler().println(player.getName() + " trades in a " + groundUnit.getName() + " for a " + commandos.getName() + " card.");
            addCardToHand(commandos);
        }
        if (MyLists.any(player.getUnitCardsInHand(), eu -> eu instanceof FightersCard)) {
            FighterAceUnitCard fighterAce = new FighterAceUnitCard();
            model.getScreenHandler().println(player.getName() + " trades in a Fighters card for a " + fighterAce.getName() + " card.");
            addCardToHand(fighterAce);
        }
    }

    public void drawEventCards(Model model) {
        EventCard card1 = model.drawEventCard();
        EventCard card2 = model.drawEventCard();
        model.getScreenHandler().println("Drew " + card1.getName() + " and " + card2.getName() + ".");
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.addOption(card1.getName() + ": " +  card1.getDescription(), (m, p) -> {
            heldEvent = card1;
        });
        multipleChoice.addOption(card2.getName() + ": " +  card2.getDescription(), (m, p) -> {
            heldEvent = card2;
        });
        multipleChoice.promptAndDoAction(model, "Which card does " + super.getName() + " keep?", this);
    }

    private String makeSimpleName() {
        return inner.getName().replace("General ", "").replace("Admiral ", "");
    }

    public void drawYourselfHorizontally(Model model, int x, int y) {
        model.getScreenHandler().drawText(makeSimpleName() + " R" + cardsInHand.size(), x, y);
        if (heldEvent != null) {
            model.getScreenHandler().drawText("Defector Ev", x, y + 1);
        } else {
            model.getScreenHandler().drawText("Defector", x, y + 1);
        }
    }

    public void drawYourselfVertically(Model model, int x, int y) {
        model.getScreenHandler().drawText(makeSimpleName(), x, y);
        model.getScreenHandler().drawText("Def", x, y+1);
        model.getScreenHandler().drawText("ect", x, y+2);
        model.getScreenHandler().drawText("or R" + cardsInHand.size(), x, y+3);
        if (heldEvent != null) {
            model.getScreenHandler().drawText(" Ev", x, y+4);
        }
    }

    @Override
    public void printHand(ScreenHandler screenHandler) {
        screenHandler.println(MyLists.frequencyList(cardsInHand, UnitCard::getNameAndStrength));
        if (heldEvent != null) {
            screenHandler.println("Held Event: " + heldEvent.getName() + ": " + heldEvent.getDescription());
        }
    }

    @Override
    public int getTotalCardsInHand() {
        return cardsInHand.size();
    }

    @Override
    public boolean takeTurn(Model model, PlayerActionState playerActionState) {
        return playerActionState.takeDefectorTurn(model, this);
    }

    public EventCard getHeldEvent() {
        return heldEvent;
    }

    public void removeHeldEvent() {
        heldEvent = null;
    }

    public List<RebelUnitCard> getRebelUnitCards() {
        return cardsInHand;
    }

    public void addUnitsToBattle(Model model) {
        MultipleChoice multipleChoice = new MultipleChoice();
        final BattleBoard[] chosenBattle = {null};
        for (BattleBoard bb : model.getBattles()) {
            multipleChoice.addOption(bb.getName(), (_, _) -> chosenBattle[0] = bb);
        }
        multipleChoice.promptAndDoAction(model, "Add units to which battle?", this);

        addUnitsToBattle(model, chosenBattle[0]);
        if (chosenBattle[0].battleIsTriggered()) {
            model.resolveBattle(chosenBattle[0]);
        }
    }

    public void addUnitsToBattle(Model model, BattleBoard battleBoard) {
        RebelUnitCard operative = MyLists.find(getRebelUnitCards(), ru -> ru instanceof RebelOperativeUnitCard);
        if (operative != null) {
            ((RebelOperativeUnitCard)operative).askToUse(model, battleBoard, this);
        }

        MultipleChoice multipleChoice = new MultipleChoice();
        for (RebelUnitCard ru : getRebelUnitCards()) {
            multipleChoice.addOption(ru.getNameAndStrength(), (m, p) -> {
                battleBoard.addRebelCard(ru);
                discardCard(m, ru);
            });
        }
        multipleChoice.addOption("Pass", (m, p) -> {});

        int count = 0;
        do  {
            multipleChoice.promptAndDoAction(model, "Which card does " + getName() + " add to " + battleBoard.getName() + "?", this);
            multipleChoice.removeSelectedOption();
            count++;
        } while (count < 2 && multipleChoice.getNumberOfChoices() > 0 &&
                !multipleChoice.getSelectedOptionName().equals("Pass"));
    }
}

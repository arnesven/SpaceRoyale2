package model.board;

import model.Model;
import model.Player;
import model.cards.*;
import util.MyLists;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.List;

public abstract class BattleBoard extends BoardLocation {

    private static final int CARD_LIMIT = 10;
    private final String name;
    private final char identifier;
    private List<RebelUnitCard> rebelUnits = new ArrayList<>();
    private List<EmpireUnitCard> empireUnits = new ArrayList<>();

    public BattleBoard(String name, char identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    public char getIdentifier() {
        return identifier;
    }

    protected abstract boolean battleSpecificResolve(Model model, List<Player> playersInBattle, boolean empireWinsSpace, boolean empireWinsGround);

    public abstract BattleBoard makeReplacement(Model model);

    @Override
    public final String getName() {
        return name + " (" + identifier + ")";
    }

    public void addRebelCard(RebelUnitCard rebelUnitCard) {
        this.rebelUnits.add(rebelUnitCard);
    }

    public void addEmpireUnit(EmpireUnitCard empireUnitCard) {
        this.empireUnits.add(empireUnitCard);
    }

    @Override
    public void drawYourself(Model model, int x, int y) {
        super.drawYourself(model, x, y);
        if (!rebelUnits.isEmpty()) {
            model.getScreenHandler().drawText("._.", x, y+2);
            model.getScreenHandler().drawText("|R|" + rebelUnits.size(), x, y+3);
            model.getScreenHandler().drawText("'¨'", x, y+4);
        }

        if (!empireUnits.isEmpty()) {
            model.getScreenHandler().drawText("._.", x+6, y+2);
            model.getScreenHandler().drawText("|E|" + empireUnits.size(), x+6, y+3);
            model.getScreenHandler().drawText("'¨'", x+6, y+4);
        }
    }

    public boolean battleIsTriggered() {
        return rebelUnits.size() >= CARD_LIMIT || empireUnits.size() >= CARD_LIMIT;
    }

    public void resolveYourself(Model model) {
        model.getScreenHandler().println("Rebel Forces are: " + freqListOrNone(rebelUnits));
        model.getScreenHandler().println("Empire Forces are: " + freqListOrNone(empireUnits));

        AlignmentCard align = model.drawBattleChanceCard();
        model.getScreenHandler().println("Drawing 1 card from Battle Chance Deck: " + align.getName());

        if (MyLists.any(rebelUnits, ru -> ru instanceof MinefieldUnitCard)) {

        }

        boolean empireWinsSpace = resolveSpaceDomain(model, rebelUnits, empireUnits, align);
        boolean empireWinsGroundDomain = resolveGroundDomain(model, rebelUnits, empireUnits, align);

        List<Player> playersInBattle = getPlayersInBattle(model);
        boolean imperialWin = battleSpecificResolve(model, playersInBattle, empireWinsSpace, empireWinsGroundDomain);
        if (imperialWin) {
            advanceWarCounter(model);
        } else {
            retreatWarCounter(model);
            for (Player p : playersInBattle) {
                model.getScreenHandler().println(p.getName() + " loses one Emperor Influence.");
                p.addToEmperorInfluence(-1);
            }
        }
        model.discardEmpireCards(empireUnits);
        if (model.checkForBattleOfCentralia()) {
            new BattleOfCentralia(model, sendUnitsToSpecialBattle(model, rebelUnits)).resolveYourself(model);
        } else if (model.checkForBattleAtRebelStronghold()) {
            new BattleAtTheRebelStronghold(model, sendUnitsToSpecialBattle(model, rebelUnits)).resolveYourself(model);
        } else {
            discardRebelCards(model, rebelUnits, imperialWin);
        }
    }

    protected void advanceWarCounter(Model model) {
        model.advanceWarCounter();
        model.getScreenHandler().println("The War counter advances to " + model.getWarCounter());
    }

    protected void retreatWarCounter(Model model) {
        model.retreatWarCounter();
        model.getScreenHandler().println("The War counter retreats to " + model.getWarCounter());
    }

    protected List<RebelUnitCard> sendUnitsToSpecialBattle(Model model, List<RebelUnitCard> rebelUnits) {
        model.discardRebelCards(rebelUnits);
        return new ArrayList<>();
    }

    private String freqListOrNone(List<? extends UnitCard> units) {
        if (units.isEmpty()) {
            return "*None*";
        }
        return MyLists.frequencyList(units, UnitCard::getNameAndStrength);
    }

    protected void discardRebelCards(Model model, List<RebelUnitCard> rebelUnits, boolean empireWin) {
        model.discardRebelCards(rebelUnits);
    }

    private List<Player> getPlayersInBattle(Model model) {
        return MyLists.filter(model.getPlayers(), p -> p.getCurrentLocation() == this);
    }

    private boolean resolveSpaceDomain(Model model, List<RebelUnitCard> rebelUnits, List<EmpireUnitCard> empireUnits, AlignmentCard battleChance) {
        int rebelSpace = MyLists.intAccumulate(rebelUnits, this::getSpaceStrength) + getSpaceBonus(battleChance);
        int empireSpace = MyLists.intAccumulate(empireUnits, this::getSpaceStrength);

        model.getScreenHandler().println("Space total (R vs E): " + rebelSpace + " vs " + empireSpace);
        if (rebelSpace > empireSpace) {
            model.getScreenHandler().println("The rebels are victorious in the space domain.");
            return false;
        }
        model.getScreenHandler().println("The empire is victorious in the space domain.");
        return true;
    }

    protected int getGroundBonus(AlignmentCard battleChance) {
        return battleChance.rebelBattleBonus();
    }

    protected int getSpaceBonus(AlignmentCard battleChance) {
        return battleChance.rebelBattleBonus();
    }

    private int getSpaceStrength(UnitCard unitCard) {
        return unitCard.isGroundUnit() ? 0 : unitCard.getStrength();
    }

    private int getGroundStrength(UnitCard unitCard) {
        return unitCard.isGroundUnit() ? unitCard.getStrength() : 0;
    }

    protected boolean resolveGroundDomain(Model model, List<RebelUnitCard> rebelUnits, List<EmpireUnitCard> empireUnits, AlignmentCard battleChance) {
        int rebelGround = MyLists.intAccumulate(rebelUnits, this::getGroundStrength) + getGroundBonus(battleChance);
        int empireGround = MyLists.intAccumulate(empireUnits, this::getGroundStrength);
        model.getScreenHandler().println("Ground total (R vs E): " + rebelGround + " vs " + empireGround);
        if (rebelGround > empireGround) {
            model.getScreenHandler().println("The rebels are victorious in the ground domain.");
            return false;
        }
        model.getScreenHandler().println("The empire are victorious in the ground domain.");
        return true;
    }

    public void printRebelUnits(Model model) {
        model.getScreenHandler().println("Rebel Units: " + freqListOrNone(rebelUnits));
    }

    public void movePlayersAfterBattle(Model model) {
        List<Player> players = MyLists.filter(model.getPlayers(), p -> p.getCurrentLocation() == this);
        if (!players.isEmpty()) {
            model.getScreenHandler().println("Moving players from " + this.getName() + ".");
        }

        for (Player p : players) {
            MultipleChoice multipleChoice = new MultipleChoice();
            multipleChoice.addOption("To Centralia (keep Shuttle)", (m, performer) -> performer.moveToLocation(m.getCentralia()));
            EmpireUnitCard shuttle = MyLists.find(p.getUnitCardsInHand(), sc -> sc instanceof ShuttleCard);
            if (shuttle != null) {
                ((ShuttleCard)shuttle).addMoveOptionsAfterBattle(model, multipleChoice, this);
            }
            if (multipleChoice.getNumberOfChoices() > 1) {
                model.getScreenHandler().println(p.getName() + " has a " + shuttle.getName() + " and may play it to move to another location than Centralia.");
            } else {
                model.getScreenHandler().println(p.getName() + " moves to Centralia.");
            }
            multipleChoice.promptAndDoAction(model, "Where do you want to move?", p);
        }
    }
}

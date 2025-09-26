package model.board;

import model.Model;
import model.Player;
import model.cards.EmpireUnitCard;
import model.cards.RebelUnitCard;
import model.cards.UnitCard;
import util.MyLists;

import java.util.ArrayList;
import java.util.List;

public abstract class BattleBoard extends BoardLocation {

    private static final int CARD_LIMIT = 8;
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
        model.getScreenHandler().println("Rebel Forces are: " + commaListOrNone(rebelUnits));
        model.getScreenHandler().println("Empire Forces are: " + commaListOrNone(empireUnits));

        boolean empireWinsSpace = resolveSpaceDomain(model, rebelUnits, empireUnits);
        boolean empireWinsGroundDomain = resolveGroundDomain(model, rebelUnits, empireUnits);
        // TODO: Draw Battle Chance

        List<Player> playersInBattle = getPlayersInBattle(model);
        boolean imperialWin = battleSpecificResolve(model, playersInBattle, empireWinsSpace, empireWinsGroundDomain);
        if (imperialWin) {
            model.advanceWarCounter();
        } else {
            for (Player p : playersInBattle) {
                model.getScreenHandler().println(p.getName() + " loses one Emperor Influence.");
                p.addToEmperorInfluence(-1);
            }
            model.retreatWarCounter();
        }
        checkForSpecialBattle(model, MyLists.filter(rebelUnits, UnitCard::isGroundUnit), imperialWin);
    }

    private String commaListOrNone(List<? extends UnitCard> units) {
        if (units.isEmpty()) {
            return "*None*";
        }
        return MyLists.commaAndJoin(units, UnitCard::getNameAndStrength);
    }

    protected void checkForSpecialBattle(Model model, List<RebelUnitCard> groundUnits, boolean empireWin) {
        // TODO: Battle at Rebel Stronghold or Battle of Centralia
    }

    private List<Player> getPlayersInBattle(Model model) {
        return MyLists.filter(model.getPlayers(), p -> p.getCurrentLocation() == this);
    }

    private boolean resolveSpaceDomain(Model model, List<RebelUnitCard> rebelUnits, List<EmpireUnitCard> empireUnits) {
        int rebelSpace = MyLists.intAccumulate(rebelUnits, this::getSpaceStrength);
        int empireSpace = MyLists.intAccumulate(empireUnits, this::getSpaceStrength);

        model.getScreenHandler().println("Space total (R vs E): " + rebelSpace + " vs " + empireSpace);
        if (rebelSpace > empireSpace) {
            model.getScreenHandler().println("The rebels are victorious in the space domain.");
            return false;
        }
        model.getScreenHandler().println("The empire are victorious in the space domain.");
        return true;
    }

    private int getSpaceStrength(UnitCard unitCard) {
        return unitCard.isGroundUnit() ? 0 : unitCard.getStrength();
    }

    private int getGroundStrength(UnitCard unitCard) {
        return unitCard.isGroundUnit() ? unitCard.getStrength() : 0;
    }

    protected boolean resolveGroundDomain(Model model, List<RebelUnitCard> rebelUnits, List<EmpireUnitCard> empireUnits) {
        int rebelGround = MyLists.intAccumulate(rebelUnits, this::getGroundStrength);
        int empireGround = MyLists.intAccumulate(empireUnits, this::getGroundStrength);
        model.getScreenHandler().println("Ground total (R vs E): " + rebelGround + " vs " + empireGround);
        if (rebelGround > empireGround) {
            model.getScreenHandler().println("The rebels are victorious in the ground domain.");
            return false;
        }
        model.getScreenHandler().println("The empire are victorious in the ground domain.");
        return true;
    }
}

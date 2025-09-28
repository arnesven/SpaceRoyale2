package model.board;

import model.Model;
import model.Player;
import model.cards.*;
import util.MyLists;
import view.MultipleChoice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class BattleBoard extends BoardLocation {

    private static final int CARD_LIMIT = 10;
    private final String name;
    private final char identifier;
    private List<RebelUnitCard> rebelUnits = new ArrayList<>();
    private List<EmpireUnitCard> empireUnits = new ArrayList<>();
    private boolean minesEffective = true;

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
        rebelUnits.sort(Comparator.comparingInt(UnitCard::getStrength));
        empireUnits.sort(Comparator.comparingInt(UnitCard::getStrength));
        print(model, "Rebel Forces are: " + freqListOrNone(rebelUnits));
        print(model, "Empire Forces are: " + freqListOrNone(empireUnits));
        printTallies(model);

        playEarlyTacticsCards(model);

        AlignmentCard align = model.drawBattleChanceCard();
        print(model, "Drawing 1 card from Battle Chance Deck: " + align.getName());

        resolveMinefields(model);

        boolean empireWinsSpace = resolveSpaceDomain(model, rebelUnits, empireUnits, align);
        boolean empireWinsGroundDomain = resolveGroundDomain(model, rebelUnits, empireUnits, align);

        List<Player> playersInBattle = getPlayersInBattle(model);
        boolean imperialWin = battleSpecificResolve(model, playersInBattle, empireWinsSpace, empireWinsGroundDomain);
        if (imperialWin) {
            advanceWarCounter(model);
        } else {
            retreatWarCounter(model);
            for (Player p : playersInBattle) {
                print(model, p.getName() + " loses one Emperor Influence.");
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

    private void printTallies(Model model) {
        print(model, "Tallies:  Space  Ground");
        int rebelSpace = MyLists.intAccumulate(rebelUnits, this::getSpaceStrength);
        int rebelGround = MyLists.intAccumulate(rebelUnits, this::getGroundStrength);
        print(model, String.format("%15d%8d", rebelSpace, rebelGround));
        int empireSpace = MyLists.intAccumulate(empireUnits, this::getSpaceStrength);
        int empireGround = MyLists.intAccumulate(empireUnits, this::getGroundStrength);
        print(model, String.format("%15d%8d", empireSpace, empireGround));
    }

    private void playEarlyTacticsCards(Model model) {
        for (Player player : MyLists.filter(model.getPlayers(), p -> !p.getTacticsCardsInHand().isEmpty())) {
            MultipleChoice multipleChoice = new MultipleChoice();
            for (TacticsCard tc : player.getTacticsCardsInHand()) {
                if ((player.getCurrentLocation() == this || tc.canBePlayedOutsideOfBattle()) && tc.playedAfterReveal()) {
                    multipleChoice.addOption(tc.getName(), (m, p) -> {
                        tc.resolve(model, p, this);
                        player.discardCard(model, tc);
                    });
                }
            }
            multipleChoice.addOption("Pass", (_, _) -> {});
            multipleChoice.promptAndDoAction(model, "Does " + player.getName() + " play a Tactics card?", player);
        }
    }

    private void resolveMinefields(Model model) {
        if (battleHasMinefields() && minesAreEffective(model)) {
            List<EmpireUnitCard> cardsToDiscard = MyLists.filter(empireUnits,
                    eu -> eu instanceof CruiserCard || eu instanceof BattleshipCard);
            if (!cardsToDiscard.isEmpty()) {
                print(model, "The rebels' minefield destroys all imperial cruisers and battleships!");
                print(model, "Discarding " + MyLists.frequencyList(cardsToDiscard, GameCard::getName));
            }
            for (EmpireUnitCard ship : cardsToDiscard) {
                empireUnits.remove(ship);
                model.discardEmpireCards(List.of(ship));
            }
        }
    }

    public boolean battleHasMinefields() {
        return MyLists.any(rebelUnits, ru -> ru instanceof MinefieldUnitCard);
    }

    protected boolean minesAreEffective(Model model) {
        return minesEffective;
    }

    protected void advanceWarCounter(Model model) {
        model.advanceWarCounter();
        print(model, "The War counter advances to " + model.getWarCounter());
    }

    protected void retreatWarCounter(Model model) {
        model.retreatWarCounter();
        print(model, "The War counter retreats to " + model.getWarCounter());
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

        print(model, "Space total (R vs E): " + rebelSpace + " vs " + empireSpace);
        if (rebelSpace > empireSpace || MyLists.all(empireUnits, UnitCard::isGroundUnit)) {
            print(model, "The rebels are victorious in the space domain.");
            return false;
        }
        print(model, "The empire is victorious in the space domain.");
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
        print(model, "Ground total (R vs E): " + rebelGround + " vs " + empireGround);
        if (rebelGround > empireGround || !MyLists.any(empireUnits, UnitCard::isGroundUnit)) {
            print(model, "The rebels are victorious in the ground domain.");
            return false;
        }
        print(model, "The empire are victorious in the ground domain.");
        return true;
    }

    public void printRebelUnits(Model model) {
        print(model, "Rebel Units: " + freqListOrNone(rebelUnits));
    }

    public void movePlayersAfterBattle(Model model) {
        List<Player> players = MyLists.filter(model.getPlayers(), p -> p.getCurrentLocation() == this);
        if (!players.isEmpty()) {
            print(model, "Moving players from " + this.getName() + ".");
        }

        for (Player p : players) {
            MultipleChoice multipleChoice = new MultipleChoice();
            multipleChoice.addOption("To Centralia (keep Shuttle)", (m, performer) -> performer.moveToLocation(m.getCentralia()));
            EmpireUnitCard shuttle = MyLists.find(p.getUnitCardsInHand(), sc -> sc instanceof ShuttleCard);
            if (shuttle != null) {
                ((ShuttleCard)shuttle).addMoveOptionsAfterBattle(model, multipleChoice, this);
            }
            if (multipleChoice.getNumberOfChoices() > 1) {
                print(model, p.getName() + " has a " + shuttle.getName() + " and may play it to move to another location than Centralia.");
            } else {
                print(model, p.getName() + " moves to Centralia.");
            }
            multipleChoice.promptAndDoAction(model, "Where do you want to move?", p);
        }
    }

    public void disableMines() {
        this.minesEffective = false;
    }

    public void useBombardment(Model model, Player player) {
        List<RebelUnitCard> cardsToDiscard = new ArrayList<>();
        List<RebelUnitCard> ground = MyLists.filter(rebelUnits, UnitCard::isGroundUnit);
        if (ground.size() <= 3) {
            cardsToDiscard.addAll(ground);
        } else {
            MultipleChoice multipleChoice = new MultipleChoice();
            for (RebelUnitCard ru : ground) {
                multipleChoice.addOption(ru.getName(), (m, p) -> {cardsToDiscard.add(ru);});
            }
            multipleChoice.promptAndDoAction(model, "Select a ground unit to discard:", player);
        }
        for (RebelUnitCard ru : cardsToDiscard) {
            print(model, "Discarding " + ru.getName() + ".");
            rebelUnits.remove(ru);
        }
        model.discardRebelCards(cardsToDiscard);
        player.addToPopularInfluence(-1);
        print(model, player.getName() + " gets -1 Popular Influence.");
    }

    public void retreatPlayer(Model model, Player player) {
        if (empireUnits.isEmpty()) {
            print(model, player.getName() + " moves to Centralia.");
            player.moveToLocation(model.getCentralia());
            return;
        }

        MultipleChoice multipleChoice = new MultipleChoice();
        for (EmpireUnitCard eu : empireUnits) {
            multipleChoice.addOption(eu.getNameAndStrength(), (m, p) -> {
                p.addCardToHand(eu);
                print(m, p.getName() + " removes " + eu.getName() + " from the battle and puts in hand.");
                print(m, p.getName() + " moves to Centralia.");
                p.moveToLocation(m.getCentralia());
            });
        }
        multipleChoice.promptAndDoAction(model, "Which card does " + player.getName() + " pick up from the battle?", player);
    }

    void print(Model model, String s) {
        model.getScreenHandler().println(s);
    }
}

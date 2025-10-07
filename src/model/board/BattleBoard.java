package model.board;

import model.Model;
import model.Player;
import model.cards.*;
import model.cards.alignment.AlignmentCard;
import model.cards.events.ChampionOfLightEventCard;
import model.cards.events.EventCard;
import model.cards.events.SuperTitanEventCard;
import model.cards.events.TacticsImprovementEventCard;
import model.cards.tactics.TacticsCard;
import model.cards.units.*;
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
    private boolean imperialWin = false;

    public BattleBoard(String name, char identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    public char getIdentifier() {
        return identifier;
    }

    protected abstract boolean battleSpecificResolve(Model model, List<Player> playersInBattle, boolean empireWinsSpace, boolean empireWinsGround);

    protected abstract void battleSpecificRewards(Model model, List<Player> playersInBattle, boolean imperialWin);

    public abstract BattleBoard flipBattleBoard();

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
        upgradeTitan(model);
        upgradeStarWarrior(model);
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
        this.setImperialWin(imperialWin);
        playLateTacticsCards(model);

        if (imperialWin) {
            advanceWarCounter(model);
        } else {
            retreatWarCounter(model);
            for (Player p : playersInBattle) {
                print(model, p.getName() + " loses one Emperor Influence.");
                p.addToEmperorInfluence(-1);
            }
        }
        battleSpecificRewards(model, playersInBattle, imperialWin);
        model.discardEmpireCards(empireUnits);
        resetUpgradedCards(model);
        if (model.checkForBattleOfCentralia()) {
            new BattleOfCentralia(model, sendUnitsToSpecialBattle(model, rebelUnits)).resolveYourself(model);
        } else if (model.checkForBattleAtRebelStronghold()) {
            new BattleAtTheRebelStronghold(model, sendUnitsToSpecialBattle(model, rebelUnits)).resolveYourself(model);
        } else {
            discardRebelCards(model, rebelUnits, imperialWin);
        }
    }

    private void setImperialWin(boolean imperialWin) {
        this.imperialWin = imperialWin;
    }

    private void upgradeTitan(Model model) {
        if (MyLists.any(model.getEventCardsInPlay(), ev -> ev instanceof SuperTitanEventCard)) {
            RebelUnitCard titan = MyLists.find(rebelUnits, ru -> ru instanceof TitanUnitCard);
            if (titan != null) {
                ((TitanUnitCard)titan).upgrade();
                model.getScreenHandler().println("Titan upgraded to " + titan.getName() + ".");
                EventCard event = MyLists.find(model.getEventCardsInPlay(),ev -> ev instanceof SuperTitanEventCard);
                model.getEventCardsInPlay().remove(event);
                model.discardEventCard(event);
            }
        }
    }

    protected void upgradeStarWarrior(Model model) {
        if (MyLists.any(model.getEventCardsInPlay(), ev -> ev instanceof ChampionOfLightEventCard)) {
            RebelUnitCard starWarrior = MyLists.find(rebelUnits, ru -> ru instanceof StarWarriorUnitCard);
            if (starWarrior != null) {
                ((StarWarriorUnitCard)starWarrior).upgrade();
                model.getScreenHandler().println("Star warrior upgraded to " + starWarrior.getName() + ".");
                EventCard event = MyLists.find(model.getEventCardsInPlay(), ev -> ev instanceof ChampionOfLightEventCard);
                model.getEventCardsInPlay().remove(event);
                model.discardEventCard(event);
            }
        }
    }

    private void resetUpgradedCards(Model model) {
        for (RebelUnitCard ru : MyLists.filter(rebelUnits, r -> r instanceof UpgradeableRebelUnitCard)) {
            ((UpgradeableRebelUnitCard)ru).reset();
        }
    }

    public void printTallies(Model model) {
        int rebelSpace = MyLists.intAccumulate(rebelUnits, this::getSpaceStrength);
        int rebelGround = MyLists.intAccumulate(rebelUnits, this::getGroundStrength);
        int empireSpace = MyLists.intAccumulate(empireUnits, this::getSpaceStrength);
        int empireGround = MyLists.intAccumulate(empireUnits, this::getGroundStrength);
        print(model,"Tallies:  Rebel  Empire");
        print(model, String.format("Space %8d%s %6d%s", rebelSpace, winAsteriskRebel(rebelSpace, empireSpace), empireSpace, winAsteriskEmpire(empireSpace, rebelSpace, !empireUnits.isEmpty())));
        print(model, String.format("Ground %7d%s %6d%s", rebelGround, winAsteriskRebel(rebelGround, empireGround), empireGround, winAsteriskEmpire(empireGround, rebelGround, !empireUnits.isEmpty())));
    }

    private String winAsteriskEmpire(int empire, int rebel, boolean atLeastOneUnit) {
        if (empire >= rebel && atLeastOneUnit) {
            return "*";
        }
        return " ";
    }

    private String winAsteriskRebel(int rebel, int empire) {
        if (rebel > empire) {
            return "*";
        }
        return " ";
    }

    public void printRebelTally(Model model) {
        int rebelSpace = MyLists.intAccumulate(rebelUnits, this::getSpaceStrength);
        int rebelGround = MyLists.intAccumulate(rebelUnits, this::getGroundStrength);
        print(model, String.format("Rebels tallies: %d Space, %d Ground", rebelSpace, rebelGround));
    }

    private void playEarlyTacticsCards(Model model) {
        for (Player player : MyLists.filter(model.getPlayers(), p -> !p.getTacticsCardsInHand().isEmpty())) {
            MultipleChoice multipleChoice = new MultipleChoice();
            for (TacticsCard tc : player.getTacticsCardsInHand()) {
                if ((player.getCurrentLocation() == this || tc.canBePlayedOutsideOfBattle()) && tc.playedAfterReveal()) {
                    multipleChoice.addOption(tc.getName(), (m, p) -> {
                        tc.resolve(model, p, this);
                        possiblyDiscardTacticsCard(model, p, tc);
                    });
                }
            }
            boolean[] done = new boolean[]{false};
            multipleChoice.addOption("Pass", (_, _) -> { done[0] = true; });
            while (!done[0] && player.getCurrentLocation() == this) { // If the player used FTL to move, no more cards can be played.
                multipleChoice.promptAndDoAction(model, "Does " + player.getName() + " play a Tactics card?", player);
                multipleChoice.removeSelectedOption();
            }
        }
    }

    private void possiblyDiscardTacticsCard(Model model, Player player, TacticsCard tc) {
        List<TacticsImprovementEventCard> tacticsImprovements = MyLists.transform(MyLists.filter(model.getEventCardsInPlay(),
                        ev -> ev instanceof TacticsImprovementEventCard),
                ev -> (TacticsImprovementEventCard)ev);
        if (!tacticsImprovements.isEmpty()) {
            if (MyLists.any(tacticsImprovements, ti -> ti.getAffectedTactics().contains(tc.getName()))) {
                if (TacticsImprovementEventCard.doesKeepCard(model, player, tc)) {
                    return;
                }
            }
        }
        player.discardCard(model, tc);
    }

    private void playLateTacticsCards(Model model) {
        for (Player player : MyLists.filter(model.getPlayers(), p -> !p.getTacticsCardsInHand().isEmpty())) {
            MultipleChoice multipleChoice = new MultipleChoice();
            for (TacticsCard tc : player.getTacticsCardsInHand()) {
                if ((player.getCurrentLocation() == this || tc.canBePlayedOutsideOfBattle()) && tc.playAfterBattle()) {
                    multipleChoice.addOption(tc.getName(), (m, p) -> {
                        tc.resolve(model, p, this);
                        possiblyDiscardTacticsCard(model, p, tc);
                    });
                }
            }
            boolean[] done = new boolean[]{false};
            multipleChoice.addOption("Pass", (_, _) -> { done[0] = true; });
            while (!done[0]) {
                multipleChoice.promptAndDoAction(model, "Does " + player.getName() + " play a Tactics card?", player);
                multipleChoice.removeSelectedOption();
            }
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
        print(model, "The War counter advances to " + model.getWarCounter() + ".");
    }

    protected void retreatWarCounter(Model model) {
        model.retreatWarCounter();
        print(model, "The War counter retreats to " + model.getWarCounter() + ".");
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

        if (getSpaceBonus(battleChance) > 0) {
            print(model, "Rebels get +" + getSpaceBonus(battleChance) + " from the Battle Chance card.");
        }
        print(model, "Space total (R vs E): " + rebelSpace + " vs " + empireSpace);
        if (rebelSpace > empireSpace || !MyLists.any(empireUnits, UnitCard::isSpaceUnit)) {
            print(model, "The Rebels are victorious in the space domain.");
            return false;
        }
        print(model, "The Empire is victorious in the space domain.");
        return true;
    }

    protected int getGroundBonus(AlignmentCard battleChance) {
        return battleChance.rebelBattleBonus();
    }

    protected int getSpaceBonus(AlignmentCard battleChance) {
        return battleChance.rebelBattleBonus();
    }

    private int getSpaceStrength(UnitCard unitCard) {
        return unitCard.isSpaceUnit() ? unitCard.getStrength() : 0;
    }

    private int getGroundStrength(UnitCard unitCard) {
        return unitCard.isGroundUnit() ? unitCard.getStrength() : 0;
    }

    protected boolean resolveGroundDomain(Model model, List<RebelUnitCard> rebelUnits, List<EmpireUnitCard> empireUnits, AlignmentCard battleChance) {
        int rebelGround = MyLists.intAccumulate(rebelUnits, this::getGroundStrength) + getGroundBonus(battleChance);
        int empireGround = MyLists.intAccumulate(empireUnits, this::getGroundStrength);
        if (getGroundBonus(battleChance) > 0) {
            print(model, "Rebels get +" + getGroundBonus(battleChance) + " from the Battle Chance card.");
        }
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

    public void printEmpireUnits(Model model) {
        print(model, "Empire Units: " + freqListOrNone(empireUnits));
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

    void print(Model model, String s) {
        model.getScreenHandler().println(s);
    }

    public List<RebelUnitCard> getRebelUnits() {
        return rebelUnits;
    }

    public List<EmpireUnitCard> getEmpireUnits() {
        return empireUnits;
    }

    public void removeUnit(RebelUnitCard ru) {
        rebelUnits.remove(ru);
    }

    public void removeUnit(EmpireUnitCard eu) {
        empireUnits.remove(eu);
    }

    public boolean canUseBombardment() {
        return true;
    }

    public boolean isEmpireVictorious() {
        return imperialWin;
    }
}

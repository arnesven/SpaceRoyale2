package model.cards.units;

public class EmpireUnitPlayerDeck extends EmpireUnitDeck {

    private static final int NO_OF_AGENT_CARDS = 2;
    private static final int NO_OF_SHUTTLE_CARDS = 2;
    private static final int NO_OF_GROUND_TROOPS_CARDS = 5;
    private static final int NO_OF_FIGHTERS_CARDS = 5;
    private static final int NO_OF_GRAV_ARMOR_CARDS = 2;
    private static final int NO_OF_CRUISER_CARDS = 3;
    private static final int NO_OF_BATTLESHIP_CARDS = 1;

    private static final EmpireUnitCard AGENT_CARD = new AgentUnitCard();
    private static final EmpireUnitCard SHUTTLE_CARD = new ShuttleCard();
    private static final EmpireUnitCard GROUND_TROOPS_CARD = new GroundTroopsCard();
    private static final EmpireUnitCard FIGHTERS_CARD = new FightersCard();
    private static final EmpireUnitCard GRAV_ARMOR_CARD = new GravArmorCard();
    private static final EmpireUnitCard CRUISER_CARD = new CruiserCard();
    private static final EmpireUnitCard BATTLESHIP_CARD = new BattleshipCard();

    public EmpireUnitPlayerDeck() {
        addCopies(AGENT_CARD, NO_OF_AGENT_CARDS);
        addCopies(SHUTTLE_CARD, NO_OF_SHUTTLE_CARDS);
        addCopies(GROUND_TROOPS_CARD, NO_OF_GROUND_TROOPS_CARDS);
        addCopies(FIGHTERS_CARD, NO_OF_FIGHTERS_CARDS);
        addCopies(GRAV_ARMOR_CARD, NO_OF_GRAV_ARMOR_CARDS);
        addCopies(CRUISER_CARD, NO_OF_CRUISER_CARDS);
        addCopies(BATTLESHIP_CARD, NO_OF_BATTLESHIP_CARDS);
        shuffle();
    }
}

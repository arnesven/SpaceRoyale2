package model.cards.units;

public class EmpireUnitPlayerDeck extends EmpireUnitDeck {

    private static final int NO_OF_AGENT_CARDS = 2;
    private static final int NO_OF_SHUTTLE_CARDS = 2;
    private static final int NO_OF_GROUND_TROOPS_CARDS = 4;
    private static final int NO_OF_FIGHTERS_CARDS = 3;
    private static final int NO_OF_GRAV_ARMOR_CARDS = 2;
    private static final int NO_OF_DESTROYER_CARDS = 2;
    private static final int NO_OF_TITAN_CARDS = 1;

    private static final EmpireUnitCard AGENT_CARD = new AgentUnitCard();
    private static final EmpireUnitCard SHUTTLE_CARD = new ShuttleCard();
    private static final EmpireUnitCard GROUND_TROOPS_CARD = new GroundTroopsCard();
    private static final EmpireUnitCard FIGHTERS_CARD = new FightersCard();
    private static final EmpireUnitCard GRAV_ARMOR_CARD = new GravArmorCard();
    private static final EmpireUnitCard DESTROYER_CARD = new DestroyerCard();
    private static final EmpireUnitCard TITAN_CARD = new TitanCard();

    public EmpireUnitPlayerDeck() {
        addCopies(AGENT_CARD, NO_OF_AGENT_CARDS);
        addCopies(SHUTTLE_CARD, NO_OF_SHUTTLE_CARDS);
        addCopies(GROUND_TROOPS_CARD, NO_OF_GROUND_TROOPS_CARDS);
        addCopies(FIGHTERS_CARD, NO_OF_FIGHTERS_CARDS);
        addCopies(GRAV_ARMOR_CARD, NO_OF_GRAV_ARMOR_CARDS);
        addCopies(DESTROYER_CARD, NO_OF_DESTROYER_CARDS);
        addCopies(TITAN_CARD, NO_OF_TITAN_CARDS);
        shuffle();
    }
}

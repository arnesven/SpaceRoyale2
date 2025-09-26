package model;

import model.cards.Deck;
import model.cards.RebelUnitCard;

public class RebelUnitDeck extends Deck<RebelUnitCard> {

    private static final int NO_OF_DRONES_CARDS = 8;
    private static final int NO_OF_REBEL_SOLDIERS_CARDS = 10;
    private static final int NO_OF_MINEFIELD_CARDS = 3;
    private static final int NO_OF_FRIGATE_CARDS = 4;
    private static final int NO_OF_STAR_WARRIOR_CARDS = 3;
    private static final int NO_OF_DESTROYER_CARDS = 4;
    private static final int NO_OF_TITAN_CARDS = 3;

    private static final RebelUnitCard DRONES = new RebelUnitCard("Drones", 1, false);
    private static final RebelUnitCard REBEL_SOLDIERS = new RebelUnitCard("Rebel Soldiers", 2, true);
    private static final RebelUnitCard MINEFIELD = new RebelUnitCard("Minefield", 0, false);
    private static final RebelUnitCard FRIGATE = new RebelUnitCard("Frigate", 3, false);
    private static final RebelUnitCard STAR_WARRIOR = new RebelUnitCard("Star Warrior", 4, true);
    private static final RebelUnitCard DESTROYER = new RebelUnitCard("Destroyer", 5, false);
    private static final RebelUnitCard TITAN = new RebelUnitCard("Titan", 7, false);

    public RebelUnitDeck() {
        addCopies(DRONES, NO_OF_DRONES_CARDS);
        addCopies(REBEL_SOLDIERS, NO_OF_REBEL_SOLDIERS_CARDS);
        addCopies(MINEFIELD, NO_OF_MINEFIELD_CARDS);
        addCopies(FRIGATE, NO_OF_FRIGATE_CARDS);
        addCopies(STAR_WARRIOR, NO_OF_STAR_WARRIOR_CARDS);
        addCopies(DESTROYER, NO_OF_DESTROYER_CARDS);
        addCopies(TITAN, NO_OF_TITAN_CARDS);
        shuffle();
    }
}

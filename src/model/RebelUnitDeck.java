package model;

import model.cards.*;
import model.cards.units.*;
import view.ScreenHandler;

public class RebelUnitDeck extends Deck<RebelUnitCard> {

    private static final int NO_OF_DRONES_CARDS = 8;
    private static final int NO_OF_REBEL_SOLDIERS_CARDS = 10;
    private static final int NO_OF_MINEFIELD_CARDS = 3;
    private static final int NO_OF_FRIGATE_CARDS = 6;
    private static final int NO_OF_STAR_WARRIOR_CARDS = 3;
    private static final int NO_OF_CRUISER_CARDS = 4;
    private static final int NO_OF_BATTLESHIP_CARDS = 3;

    private static final RebelUnitCard DRONES = new DronesUnitCard();
    private static final RebelUnitCard REBEL_SOLDIERS = new RebelSoldiersCard();
    private static final RebelUnitCard MINEFIELD = new MinefieldUnitCard();
    private static final RebelUnitCard FRIGATE = new FrigateUnitCard();
    private static final RebelUnitCard STAR_WARRIOR = new StarWarriorUnitCard();
    private static final RebelUnitCard CRUISER = new CruiserCard();
    private static final RebelUnitCard BATTLESHIP = new BattleshipCard();

    public RebelUnitDeck() {
        addCopies(DRONES, NO_OF_DRONES_CARDS);
        addCopies(REBEL_SOLDIERS, NO_OF_REBEL_SOLDIERS_CARDS);
        addCopies(MINEFIELD, NO_OF_MINEFIELD_CARDS);
        addCopies(FRIGATE, NO_OF_FRIGATE_CARDS);
        addCopies(STAR_WARRIOR, NO_OF_STAR_WARRIOR_CARDS);
        addCopies(CRUISER, NO_OF_CRUISER_CARDS);
        addCopies(BATTLESHIP, NO_OF_BATTLESHIP_CARDS);
        shuffle();
    }

    public void drawYourself(ScreenHandler screenHandler, int x, int y) {
        if (!isEmpty()) {
            screenHandler.drawText(".-.", x, y);
            screenHandler.drawText("|R|" + size(), x, y + 1);
            screenHandler.drawText("'¨'", x, y + 2);
        }
    }
}

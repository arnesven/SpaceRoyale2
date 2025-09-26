package model;

import view.ScreenHandler;

public class GameTracks {
    private static final int MAX_HEALTH = 14;
    private static final int MAX_UNREST = 10;

    private int turn;
    private int unrest;
    private int health;
    private int war;

    public GameTracks(Model model) {
        this.turn = 1;
        this.unrest = 0;
        this.health = MAX_HEALTH;
        this.war = 1;
    }

    public void printYourself(ScreenHandler screenHandler) {
        screenHandler.println("Current Turn: " + turn);
        screenHandler.println("Current Unrest: " + unrest + "/" + MAX_UNREST);
        screenHandler.println("Current Emperor Health: " + health);
        screenHandler.println("Current War Advantage: " + war);
    }

    public int getTurn() {
        return turn;
    }

    public void drawYourself(Model model, int x, int y) {
        model.getScreenHandler().drawText("Turn: " + turn, x, y);
        model.getScreenHandler().drawText("Unrest: " + unrest + "/" + MAX_UNREST, x, y+1);
        model.getScreenHandler().drawText("Emperor Health: " + health, x, y+2);
        model.getScreenHandler().drawText("War Advantage: " + war, x, y+3);
    }

    public void addToUnrest(int i) {
        unrest += i;
    }

    public boolean isUnrestMaxedOut() {
        return unrest >= MAX_UNREST;
    }

    public void addToWar(int i) {
        war += i;
    }
}

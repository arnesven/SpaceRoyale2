package model;

import view.ScreenHandler;

public class GameTracks {
    public static final int MAX_GAME_TURN = 8;
    private static final int MAX_HEALTH = 14;
    private static final int MAX_UNREST = 10;
    private static final int BATTLE_OF_CENTRALIA_SPACE = -6;
    private static final int BATTLE_AT_REBEL_STRONGHOLD_SPACE = 6;

    private int turn;
    private int unrest;
    private int health;
    private int war;

    public GameTracks(Model model) {
        this.turn = 1;
        this.unrest = 0;
        this.health = 0;
        this.war = 0; // TODO: 0
    }

    public void printYourself(ScreenHandler screenHandler) {
        screenHandler.println("Current Turn: " + turn);
        screenHandler.println("Current Unrest: " + unrest + "/" + MAX_UNREST);
        screenHandler.println("Current Emperor Health: " + health + "/" + MAX_HEALTH);
        screenHandler.println("Current War Advantage: " + war);
    }

    public int getTurn() {
        return turn;
    }

    public void drawYourself(Model model, int x, int y) {
        model.getScreenHandler().drawText("Turn: " + turn, x, y);
        model.getScreenHandler().drawText("Unrest: " + unrest + "/" + MAX_UNREST, x, y+1);
        model.getScreenHandler().drawText("Emperor Health: " + health + " " + healthCategory(health), x, y+2);
        model.getScreenHandler().drawText("War Advantage: " + war, x, y+3);
    }

    private String healthCategory(int health) {
        if (health < 8) {
            return "(Healthy)";
        }
        if (health >= MAX_HEALTH) {
            return "(Dead)";
        }
        return "(Decline)";
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

    public int getUnrest() {
        return unrest;
    }

    public static int getMaxUnrest() {
        return MAX_UNREST;
    }

    public void addToHealth(int i) {
        health += i;
    }

    public boolean isEmperorDeath() {
        return health >= MAX_HEALTH;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    public void incrementTurn() {
        turn++;
    }

    public int getWar() {
        return war;
    }

    public boolean isBattleOfCentralia() {
        return war == BATTLE_OF_CENTRALIA_SPACE;
    }

    public boolean isBattleAtTheRebelStronghold() {
        return war == BATTLE_AT_REBEL_STRONGHOLD_SPACE;
    }

    public void setWarCounter(int i) {
        war = i;
    }
}

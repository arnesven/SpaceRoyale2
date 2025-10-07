package model;

import model.board.BattleBoard;
import model.tracks.HealthCategory;
import model.tracks.WarTrack;
import view.ScreenHandler;

import java.io.Serializable;

public class GameTracks implements Serializable {
    public static final int MAX_GAME_TURN = 8;
    private static final int MAX_HEALTH = 14;
    private static final int MAX_UNREST = 10;

    private int turn;
    private int unrest;
    private int health;
    private WarTrack war;

    public GameTracks(Model model) {
        this.turn = 1;
        this.unrest = 0;
        this.health = 0;
        this.war = new WarTrack();
    }

    public void printYourself(ScreenHandler screenHandler) {
        screenHandler.println("Current Turn: " + turn);
        screenHandler.println("Current Unrest: " + unrest + "/" + MAX_UNREST);
        screenHandler.println("Current Emperor Health: " + health + "/" + MAX_HEALTH);
        screenHandler.println("Current War Advantage: " + war.asString());
    }

    public int getTurn() {
        return turn;
    }

    public void drawYourself(Model model, int x, int y) {
        model.getScreenHandler().drawText("Turn: " + turn, x, y);
        model.getScreenHandler().drawText("Unrest: " + unrest + "/" + MAX_UNREST, x, y+1);
        model.getScreenHandler().drawText("Emperor Health: " + health + " " + healthCategory(health), x, y+2);
        model.getScreenHandler().drawText("War Advantage: " + war.asString(), x, y+3);
    }

    private HealthCategory healthCategory(int health) {
        if (health < 8) {
            return HealthCategory.HEALTHY;
        }
        if (health >= MAX_HEALTH) {
            return HealthCategory.DEATH;
        }
        return HealthCategory.DECLINE;
    }

    public void addToUnrest(int i) {
        unrest = Math.max(0, unrest + i);
    }

    public boolean isUnrestMaxedOut() {
        return unrest >= MAX_UNREST;
    }

    public void addToWar(int i) {
        war.moveCounter(i);
    }

    public int getUnrest() {
        return unrest;
    }

    public static int getMaxUnrest() {
        return MAX_UNREST;
    }

    public void addToHealth(int i) {
        health = Math.max(0, health + i);
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
        war.setTurn(turn);
    }

    public int getWar() {
        return war.getCounter();
    }

    public boolean isBattleOfCentralia() {
        return war.isBattleOfCentralia(turn);
    }

    public boolean isBattleAtTheRebelStronghold() {
        return war.isBattleAtRebelStronghold(turn);
    }

    public void setWarCounter(int i) {
        war.setCounter(i);
    }

    public BattleBoard replaceBattleBoard(Model model, BattleBoard bb) {
        return war.replaceBoard(bb);
    }

    public void setUnrest(int i) {
        unrest = i;
    }

    public void setEmperorHealth(int health) {
        this.health = health;
    }

    public boolean isEmperorHealthy() {
        return healthCategory(health) == HealthCategory.HEALTHY;
    }
}

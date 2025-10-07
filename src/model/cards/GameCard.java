package model.cards;

import java.io.Serializable;

public abstract class GameCard implements Serializable {

    private String name;

    public GameCard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public abstract GameCard copy();
}

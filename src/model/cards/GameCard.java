package model.cards;

public abstract class GameCard {

    private String name;

    public GameCard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public abstract GameCard copy();
}

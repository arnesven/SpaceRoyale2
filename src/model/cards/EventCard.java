package model.cards;

import model.Model;
import model.Player;

public abstract class EventCard extends GameCard {
    private final boolean isMandatory;
    private final String description;

    public EventCard(String name, boolean isMandatory, String description) {
        super(name);
        this.description = description;
        this.isMandatory = isMandatory;
    }

    public boolean staysInPlay() { return false;}

    public abstract void resolve(Model model, Player player);

    public String getDescription() {
        return description;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public String getNameAndMandatory() {
        return getName() + (isMandatory() ? " (mandatory)" : "");
    }
}

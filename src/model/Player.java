package model;

import model.board.BoardLocation;
import model.cards.*;
import view.ScreenHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player {
    private final MyColors color;
    private final String name;
    private final char shortName;
    private final EmpireUnitPlayerDeck unitDeck;
    private int emperorInfluence;
    private int popularInfluence;
    private BoardLocation currentLocation = null;
    private AlignmentCard loyalty;
    private List<EmpireUnitCard> unitCardsInHand = new ArrayList<>();
    private List<TacticsCard> tacticsCardsInHand = new ArrayList<>();

    public Player(MyColors color, String name, char shortName) {
        this.color = color;
        this.name = name;
        this.shortName = shortName;
        this.unitDeck = new EmpireUnitPlayerDeck();
        this.emperorInfluence = 0;
        this.popularInfluence = 0;
    }

    public String getName() {
        return name;
    }

    public String getNameWithShort() {
        return getName() + " (" + shortName + ")";
    }

    public void moveToLocation(BoardLocation location) {
        if (currentLocation != null) {
            currentLocation.removePlayer(this);
        }
        currentLocation = location;
        location.addPlayer(this);
    }

    public EmpireUnitPlayerDeck getUnitDeck() {
        return unitDeck;
    }

    public void setLoyaltyCard(AlignmentCard loyalty) {
        this.loyalty = loyalty;
    }

    public void drawUnitCard(Model model) {
        // TODO: if unitDeck empty, draw from communal.
        this.unitCardsInHand.add(unitDeck.drawOne());
    }

    public void printHand(ScreenHandler screenHandler) {
        StringBuilder bldr = new StringBuilder();
        for (GameCard c : unitCardsInHand) {
            bldr.append(c.getName()).append(", ");
        }
        for (GameCard c : tacticsCardsInHand) {
            bldr.append(c.getName()).append(", ");
        }
        if (bldr.isEmpty()) {
            screenHandler.println("*None*");
        } else {
            screenHandler.println(bldr.substring(0, bldr.length() - 2));
        }
    }

    public String getShortName() {
        return shortName + "";
    }

    public BoardLocation getCurrentLocation() {
        return currentLocation;
    }

    public void drawUnitCards(Model model, int count) {
        for (int j = 0; j < count; ++j) {
            drawUnitCard(model);
        }
    }

    public void drawTacticsCard(Model model) {
        this.tacticsCardsInHand.add(model.getTacticsDeck().drawOne());
    }

    public List<EmpireUnitCard> getUnitCardsInHand() {
        return unitCardsInHand;
    }

    public void removeUnitCardFromHand(EmpireUnitCard eu) {
        unitCardsInHand.remove(eu);
    }

    public void addToPopularInfluence(int i) {
        popularInfluence += i;
    }

    public void addToEmperorInfluence(int i) {
        emperorInfluence -= i;
    }
}

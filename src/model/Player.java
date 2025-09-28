package model;

import model.board.BoardLocation;
import model.cards.*;
import util.MyLists;
import util.MyStrings;
import view.ScreenHandler;

import java.util.*;

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
        if (unitDeck.isEmpty()) {
            this.unitCardsInHand.add(model.drawCommunalEmpireUnitCard());
        } else {
            this.unitCardsInHand.add(unitDeck.drawOne());
        }
        unitCardsInHand.sort(Comparator.comparingInt(UnitCard::getStrength));
    }

    public void printHand(ScreenHandler screenHandler) {
        List<GameCard> cards = new ArrayList<>();
        cards.addAll(unitCardsInHand);
        cards.addAll(tacticsCardsInHand);
        if (cards.isEmpty()) {
            screenHandler.println("*None*");
        } else {
            String unitString = MyLists.frequencyList(unitCardsInHand, UnitCard::getNameAndStrength);
            String tacticsString = MyLists.commaAndJoin(tacticsCardsInHand, GameCard::getName);
            String strToPrint;
            if (!unitCardsInHand.isEmpty() && !tacticsCardsInHand.isEmpty()) {
                strToPrint = unitString + ", " + tacticsString;
            } else {
                if (unitCardsInHand.isEmpty()) {
                    strToPrint = tacticsString;
                } else {
                    strToPrint = unitString;
                }
            }
            screenHandler.println(strToPrint);
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
        if (!model.getTacticsDeck().isEmpty()) {
            this.tacticsCardsInHand.add(model.getTacticsDeck().drawOne());
        } else {
            model.getScreenHandler().println("The Tactics Deck is empty!"); // TODO: reshuffle discard
        }
    }

    public List<EmpireUnitCard> getUnitCardsInHand() {
        return unitCardsInHand;
    }

    public void addToPopularInfluence(int i) {
        popularInfluence += i;
    }

    public void addToEmperorInfluence(int i) {
        emperorInfluence -= i;
    }

    public AlignmentCard getLoyaltyCard() {
        return loyalty;
    }

    public List<TacticsCard> getTacticsCardsInHand() {
        return tacticsCardsInHand;
    }

    public void discardCard(Model model, EmpireUnitCard eu) {
        removeUnitCardFromHand(eu);
        model.discardEmpireCards(List.of(eu));
    }

    public void discardCard(Model model, TacticsCard tc) {
        tacticsCardsInHand.remove(tc);
        model.discardTacticsCards(List.of(tc));
    }

    public void removeUnitCardFromHand(EmpireUnitCard eu) {
        unitCardsInHand.remove(eu);
    }

    public void addCardToHand(EmpireUnitCard eu) {
        unitCardsInHand.add(eu);
    }
}

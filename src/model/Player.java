package model;

import model.board.BoardLocation;
import model.cards.*;
import model.cards.alignment.AlignmentCard;
import model.cards.alignment.RebelAlignmentCard;
import model.cards.tactics.TacticsCard;
import model.cards.units.EmpireUnitCard;
import model.cards.units.EmpireUnitPlayerDeck;
import model.cards.units.UnitCard;
import model.states.PlayerActionState;
import util.MyLists;
import view.ScreenHandler;

import java.io.Serializable;
import java.util.*;

public class Player implements Serializable {
    private final MyColors color;
    private final String name;
    private final char shortName;
    private BoardLocation currentLocation = null;
    private final EmpireUnitPlayerDeck unitDeck;
    private int emperorInfluence;
    private int popularInfluence;
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

    public MyColors getColor() {
        return color;
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
            if (!unitCardsInHand.isEmpty()) {
                screenHandler.println(MyLists.frequencyList(unitCardsInHand, UnitCard::getNameAndStrength));
            }
            if (!tacticsCardsInHand.isEmpty()) {
                screenHandler.println(MyLists.frequencyList(tacticsCardsInHand, GameCard::getName));
            }
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
        this.tacticsCardsInHand.add(model.drawTacticsCard());
    }

    public List<EmpireUnitCard> getUnitCardsInHand() {
        return unitCardsInHand;
    }

    public void addToPopularInfluence(int i) {
        popularInfluence += i;
    }

    public void addToEmperorInfluence(int i) {
        emperorInfluence += i;
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
        model.discardTacticsCards(tc);
    }

    public void removeUnitCardFromHand(EmpireUnitCard eu) {
        unitCardsInHand.remove(eu);
    }

    public void addCardToHand(EmpireUnitCard eu) {
        unitCardsInHand.add(eu);
        unitCardsInHand.sort(Comparator.comparingInt(UnitCard::getStrength));
    }

    public void addCardToHand(TacticsCard tc) {
        tacticsCardsInHand.add(tc);
    }

    protected String getSimpleName() {
        return name.replace("General ", "").replace("Admiral ", "");
    }

    public void drawYourselfHorizontally(Model model, int x, int y) {
        char loy = loyalty instanceof RebelAlignmentCard ? 'R':'E';
        model.getScreenHandler().drawText(getSimpleName() + " " + getCardSymbol(model) + + unitCardsInHand.size() + " T" + tacticsCardsInHand.size() + " " + loy, x, y);
        model.getScreenHandler().drawText("EI:" + emperorInfluence + " PI:" + popularInfluence + " Deck:" + unitDeck.size(), x, y+1);
    }

    private String getCardSymbol(Model model) {
        return model.hasCollaborativelyDrawnThisTurn(this) ?  "ë" : "E";
    }

    public void drawYourselfVertically(Model model, int x, int y, boolean compact) {
        char loy = loyalty instanceof RebelAlignmentCard ? 'R':'E';
        model.getScreenHandler().drawText(getSimpleName(), x, y);
        model.getScreenHandler().drawText(getCardSymbol(model) + unitCardsInHand.size() + " T" + tacticsCardsInHand.size(), x, y + 1);
        if (compact) {
            model.getScreenHandler().drawText(String.format("EI:%d PI:%d " + loy, emperorInfluence, popularInfluence), x, y + 2);
            model.getScreenHandler().drawText("Deck:" + unitDeck.size(), x, y + 3);

        } else {
            model.getScreenHandler().drawText("EI:" + emperorInfluence, x, y + 2);
            model.getScreenHandler().drawText("PI:" + popularInfluence, x, y + 3);
            model.getScreenHandler().drawText("Deck:", x, y + 4);
            model.getScreenHandler().drawText(loy + " " + unitDeck.size(), x, y + 5);
        }
    }

    public void drawYourselfVertically(Model model, int x, int y) {
        drawYourselfVertically(model, x, y, false);
    }

    public int getPopularInfluence() {
        return popularInfluence;
    }

    public int getTotalCardsInHand() {
        return getUnitCardsInHand().size() + getTacticsCardsInHand().size();
    }

    public int getEmperorInfluence() {
        return emperorInfluence;
    }

    public boolean takeTurn(Model model, PlayerActionState playerActionState) {
        return playerActionState.takeNormalPlayerTurn(model, this);
    }

    public void removeTacticsCardFromHand(TacticsCard tc) {
        tacticsCardsInHand.remove(tc);
    }
}

package model;

import model.board.GameBoard;
import model.cards.*;

import java.util.ArrayList;
import java.util.List;

public class GameData {
    public GameBoard gameBoard;
    public List<Player> players;
    public Deck<AlignmentCard> battleChanceDeck;
    public RebelUnitDeck rebelUnitDeck;
    public List<RebelUnitCard> rebelUnitDiscard = new ArrayList<>();
    public CommonEmpireUnitDeck commonEmpireUnitDeck;
    public List<EmpireUnitCard> empireUnitDiscard = new ArrayList<>();
    public TacticsDeck tacticsDeck;
    public List<TacticsCard> tacticsDiscard = new ArrayList<>();
    public GameTracks gameTracks;
    public Player currentPlayer;
    public EventDeck eventDeck;
    public List<EventCard> eventDiscard = new ArrayList<>();
    public int initialRebelUnitRate = 3;
}

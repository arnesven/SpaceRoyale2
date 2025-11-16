package model;

import model.board.GameBoard;
import model.cards.*;
import model.cards.events.EventCard;
import model.cards.events.EventDeck;
import model.cards.special.SpecialEventCard;
import model.cards.tactics.TacticsCard;
import model.cards.tactics.TacticsDeck;
import model.cards.units.CommonEmpireUnitDeck;
import model.cards.units.EmpireUnitCard;
import model.cards.units.RebelUnitCard;
import model.states.GameState;
import model.states.InitialGameState;
import model.states.SpecialEvents;

import java.io.Serializable;
import java.util.*;

public class GameData implements Serializable {
    public GameBoard gameBoard;
    public List<Player> players;
    public BattleChanceDeck battleChanceDeck;
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
    public List<EventCard> eventCardsInPlay = new ArrayList<>();
    public int initialRebelUnitRate = 3;
    public GameState currentState = new InitialGameState();
    public Set<Player> collaborativeDraw = new HashSet<>();
    public SpecialEvents specialEvents = new SpecialEvents();
}

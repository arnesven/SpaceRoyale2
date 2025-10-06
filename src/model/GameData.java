package model;

import model.board.BoardLocation;
import model.board.GameBoard;
import model.cards.*;
import model.cards.alignment.AlignmentCard;
import model.cards.events.EventCard;
import model.cards.events.EventDeck;
import model.cards.tactics.TacticsCard;
import model.cards.tactics.TacticsDeck;
import model.cards.units.CommonEmpireUnitDeck;
import model.cards.units.EmpireUnitCard;
import model.cards.units.RebelUnitCard;

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
    public List<EventCard> eventCardsInPlay = new ArrayList<>();
    public int initialRebelUnitRate = 3;
}

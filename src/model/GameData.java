package model;

import model.board.GameBoard;
import model.cards.*;

import java.util.List;

public class GameData {
    public RebelUnitDeck rebelUnitDeck;
    public GameBoard gameBoard;
    public List<Player> players;
    public Deck<AlignmentCard> battleChanceDeck;
    public TacticsDeck tacticsDeck;
    public GameTracks gameTracks;
    public Player currentPlayer;
    public List<EmpireUnitCard> unitDiscard;
    public List<TacticsCard> tacticsDiscard;
}

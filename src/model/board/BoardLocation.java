package model.board;

import model.Model;
import model.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BoardLocation implements Serializable {
    private List<Player> occupants = new ArrayList<>();

    public void removePlayer(Player player) {
        this.occupants.remove(player);
    }

    public void addPlayer(Player player) {
        this.occupants.add(player);
    }

    public abstract String getName();

    public void drawYourself(Model model, int x, int y) {
        model.getScreenHandler().drawText(getName(), x, y);
        drawPlayers(model, x, y+1);
    }

    private void drawPlayers(Model model, int x, int y) {
        int col = x;
        Map<Player, Boolean> playersPresent = new HashMap<>();
        for (Player p : model.getPlayers()) {
            playersPresent.put(p, p.getCurrentLocation() == this);
        }
        for (Player p : playersPresent.keySet()) {
            model.getScreenHandler().drawText(playersPresent.get(p) ? p.getShortName() : "-", col++, y);
        }
    }
}

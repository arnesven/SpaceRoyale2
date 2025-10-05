package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SuccessionEnding {

    private static int comparePlayers(Player o1, Player o2) {
        return scorePlayer(o2) - scorePlayer(o1);
    }

    private static int scorePlayer(Player o1) {
        return o1.getEmperorInfluence() * 10000 +
                o1.getPopularInfluence() * 100 +
                o1.getTotalCardsInHand();
    }

    public static WinCondition makeWinCondition(Model model) {
        List<Player> playersSorted = new ArrayList<>(model.getPlayers());
        playersSorted.sort(SuccessionEnding::comparePlayers);
        for (Player p : playersSorted) {
            model.getScreenHandler().println(p.getName() + " has " + p.getEmperorInfluence() + " EI, " +
                    p.getPopularInfluence() + " PI and " + p.getTotalCardsInHand() + " cards in hand.");
        }
        HashSet<Player> winners = new HashSet<>();
        for (Player p : playersSorted) {
            if (comparePlayers(p, playersSorted.getFirst()) == 0) {
                winners.add(playersSorted.getFirst());
            }
        }
        return winners::contains;
    }
}

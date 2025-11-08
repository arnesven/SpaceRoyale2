import model.Model;
import model.cards.alignment.RebelAlignmentCard;
import util.MyLists;
import view.ScreenHandler;
import view.SilentScreenHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RebelCountProbabilities {

    private static final int NO_OF_SIMULATIONS = 100000;

    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "US"));
        ScreenHandler screenHandler = new SilentScreenHandler();
        Map<Integer, Map<Integer, Integer>> table = new HashMap<>();
        for (int players = 5; players < 9; ++players) {
            Map<Integer, Integer> freqMap = new HashMap<>();
            table.put(players, freqMap);
            for (int i = 0; i < NO_OF_SIMULATIONS; ++i) {
                Model model = new Model(screenHandler);
                model.setUpBoards();
                model.setUpPlayers(players);
                int rebels = MyLists.intAccumulate(model.getPlayers(), p -> p.getLoyaltyCard() instanceof RebelAlignmentCard ? 1 : 0);
                //System.out.println("In a " + players + " player game, " + rebels + " rebels.");
                if (!freqMap.containsKey(rebels)) {
                    freqMap.put(rebels, 0);
                }
                freqMap.put(rebels, freqMap.get(rebels) + 1);
            }
        }
        System.out.println("");

        System.out.print(" ");
        for (int players = 5; players < 9; ++players) {
            System.out.print("  " + players + " Plrs");
        }
        System.out.println("");
        for (int rebels = 0; rebels < 4; ++rebels) {
            System.out.print(rebels);
            for (int players = 5; players < 9; ++players) {
                if (rebels < 3 || players == 8) {
                    double probability = table.get(players).get(rebels) * 100.0 / NO_OF_SIMULATIONS;
                    System.out.printf("%8.2f", probability);
                } else {
                    System.out.print("    -   ");
                }

            }
            System.out.println("");
        }
    }

}

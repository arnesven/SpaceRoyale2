package model.board;

import model.Model;
import model.RebelUnitDeck;
import model.cards.EmpireUnitCard;
import model.cards.RebelUnitCard;

import java.util.ArrayList;
import java.util.List;

public abstract class BattleBoard extends BoardLocation {

    private final String name;
    private final char identifier;
    private List<RebelUnitCard> rebelUnits = new ArrayList<>();
    private List<EmpireUnitCard> empireUnits = new ArrayList<>();

    public BattleBoard(String name, char identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    @Override
    public final String getName() {
        return name + " (" + identifier + ")";
    }

    public void addRebelCard(RebelUnitCard rebelUnitCard) {
        this.rebelUnits.add(rebelUnitCard);
    }

    public void addEmpireUnit(EmpireUnitCard empireUnitCard) {
        this.empireUnits.add(empireUnitCard);
    }

    @Override
    public void drawYourself(Model model, int x, int y) {
        super.drawYourself(model, x, y);
        if (!rebelUnits.isEmpty()) {
            model.getScreenHandler().drawText("._.", x, y+2);
            model.getScreenHandler().drawText("|R|" + rebelUnits.size(), x, y+3);
            model.getScreenHandler().drawText("'¨'", x, y+4);
        }

        if (!empireUnits.isEmpty()) {
            model.getScreenHandler().drawText("._.", x+4, y+2);
            model.getScreenHandler().drawText("|E|" + empireUnits.size(), x+4, y+3);
            model.getScreenHandler().drawText("'¨'", x+4, y+4);
        }
    }

    public boolean battleIsTriggered() {
        return rebelUnits.size() >= 10 || empireUnits.size() >= 10;
    }

    public void resolveYourself(Model model) {
        model.getScreenHandler().println("Rebel Forces are: ");

        for (RebelUnitCard ru : rebelUnits) {

        }
    }
}

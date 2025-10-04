package model.cards.units;

import model.cards.Deck;
import view.ScreenHandler;

import java.util.List;

public class CommonEmpireUnitDeck extends Deck<EmpireUnitCard> {

    public CommonEmpireUnitDeck(List<EmpireUnitCard> empireUnitDiscard) {
        for (EmpireUnitCard eu : empireUnitDiscard) {
            addCard(eu);
        }
        shuffle();
    }

    public void drawYourself(ScreenHandler screenHandler, int x, int y) {
        if (!isEmpty()) {
            screenHandler.drawText(".-.", x, y + 1);
            screenHandler.drawText("|E|" + size(), x, y + 2);
            screenHandler.drawText("'¨'", x, y + 3);
        }
    }
}

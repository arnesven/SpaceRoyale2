package model.cards;

import model.cards.alignment.AlignmentCard;
import view.ScreenHandler;

public class BattleChanceDeck extends Deck<AlignmentCard> {

    public void drawYourself(ScreenHandler screenHandler, int x, int y) {
        if (!isEmpty()) {
            screenHandler.drawText(".--.", x, y);
            screenHandler.drawText("|BC|" + size(), x, y + 1);
            screenHandler.drawText("'¨¨'", x, y + 2);
        }
    }
}

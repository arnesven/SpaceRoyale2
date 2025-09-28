package model.cards;

import view.ScreenHandler;

public class TacticsDeck extends Deck<TacticsCard> {

    private static final int NO_OF_BOMBARDMENT_CARDS = 4;
    private static final int NO_OF_ALL_HAIL_CARDS = 3;
    private static final int NO_OF_FTL_RETREAT_CARDS = 3;
    private static final int NO_OF_REINFORCE_CARDS = 3;
    private static final int NO_OF_EVASIVE_CARDS = 3;
    private static final int NO_OF_MASS_DRIVER_CARDS = 3;
    private static final int NO_OF_ZOTRIAN_ARMOR_CARDS = 3;
    private static final int NO_OF_MINEFIELD_CODES_CARDS = 4;
    private static final int NO_OF_HUMANITARIAN_CARDS = 2;

    private static final TacticsCard BOMBARDMENT_CARD = new BombardmentCard();
    private static final TacticsCard ALL_HAIL_THE_EMPEROR_CARD = new AllHailTheEmperorCard();
    private static final TacticsCard FTL_RETREAT_CARD = new FTLRetreatCard();
    private static final TacticsCard REINFORCE_CARD = new ReinforceCard();
    private static final TacticsCard EVASIVE_MANEUVERS_CARD = new EvasiveManeuversCard();
    private static final TacticsCard MASS_DRIVER_CANNON_CARD = new MassDriverCannonCard();
    private static final TacticsCard ZOTRIAN_ARMOR_CARD = new ZotrianArmorCard();
    private static final TacticsCard MINEFIELD_CODES_CARD = new MinefieldCodesCard();
    private static final TacticsCard HUMANITARIAN_AID_CARD = new HumanitarianAidCard();

    public TacticsDeck() {
        addCopies(BOMBARDMENT_CARD, NO_OF_BOMBARDMENT_CARDS);
        addCopies(ALL_HAIL_THE_EMPEROR_CARD, NO_OF_ALL_HAIL_CARDS);
        addCopies(FTL_RETREAT_CARD, NO_OF_FTL_RETREAT_CARDS);
        addCopies(REINFORCE_CARD, NO_OF_REINFORCE_CARDS);
        addCopies(EVASIVE_MANEUVERS_CARD, NO_OF_EVASIVE_CARDS);
        addCopies(MASS_DRIVER_CANNON_CARD, NO_OF_MASS_DRIVER_CARDS);
        addCopies(ZOTRIAN_ARMOR_CARD, NO_OF_ZOTRIAN_ARMOR_CARDS);
        addCopies(MINEFIELD_CODES_CARD, NO_OF_MINEFIELD_CODES_CARDS);
        addCopies(HUMANITARIAN_AID_CARD, NO_OF_HUMANITARIAN_CARDS);
        shuffle();
    }

    public void drawYourself(ScreenHandler screenHandler, int x, int y) {
        if (!isEmpty()) {
            screenHandler.drawText(".-.", x, y);
            screenHandler.drawText("|T|" + size(), x, y + 1);
            screenHandler.drawText("'¨'", x, y + 2);
        }
    }
}

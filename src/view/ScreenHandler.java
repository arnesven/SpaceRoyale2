package view;

import java.util.Scanner;

public class ScreenHandler {

    private static final String DRAW_AREA_ROW =
            "                                                                                          ";
    private static final int DRAWING_AREA_HEIGHT = 16;
    private StringBuffer[] drawingArea;

    public ScreenHandler() {
        clearDrawingArea();
    }

    public void println(String text) {
        System.out.println(text);
    }

    public void print(String prompt) {
        System.out.print(prompt);
    }

    public void printDrawingArea() {
        for (StringBuffer s : drawingArea) {
            println(s.toString());
        }
    }

    public void drawText(String text, int x, int y) {
        for (int i = 0; i < text.length(); ++i) {
            if (x+i < drawingArea[y].length()) {
                drawingArea[y].setCharAt(x + i, text.charAt(i));
            }
        }
    }

    public void clearDrawingArea() {
        drawingArea = new StringBuffer[DRAWING_AREA_HEIGHT];
        for (int i = 0; i < drawingArea.length; ++i) {
            drawingArea[i] = new StringBuffer(DRAW_AREA_ROW);
        }
    }

    public int integerInput() {
        return new Scanner(System.in).nextInt();
    }

    public String lineInput() {
        return new Scanner(System.in).nextLine();
    }
}

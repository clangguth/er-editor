package Shapes;

import java.awt.*;

/**
 * <p>Title: Handle</p>
 * <p>Description: A class representing a visible handle (square)</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 20/06/2003
 */
public class Handle {

    /* Location points */
    private int fX, fY;

    /**
     * Constructor
     *
     * @param x X-coördinate
     * @param y Y-coördinate
     */
    public Handle(final int x, final int y) {
        fX = x;
        fY = y;
    }

    /**
     * Draws the handle
     *
     * @param x  X-coördinate for the translation
     * @param y  Y-coördinate for the translation
     * @param g2 Place to draw on
     */
    public void draw(final int x, final int y, final Graphics g2) {
        g2.fillRect(x + fX - (5 / 2), y + fY - (5 / 2), 5, 5);
        g2.drawRect(x + fX - (5 / 2), y + fY - (5 / 2), 5, 5);
    }

}
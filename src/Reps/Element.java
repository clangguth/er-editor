package Reps;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>Title: AttributedElement</p>
 * <p>Description: A base class representing an internal element</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 19/06/2003
 */
abstract public class Element {

    /* X Coordinate of the element */
    private int fX;

    /* Y Coordinate of the element */
    private int fY;

    /* Holds the number of elements made so far */
    private static int fElementCount = 0;

    /* Unique ID to identify this element */
    private int fId = fElementCount++;

    /* Name of the Element */
    private String fName = "";

    /**
     * Constructor
     *
     * @param name Name of the Element
     */
    public Element(final String name) {
        fName = name;
    }

    /**
     * Set the name of the Element
     *
     * @param name Name to set
     * @throws Exception
     */
    public void setName(final String name) throws Exception {
        fName = name;
    }

    /**
     * Returns the unique ID of this element
     *
     * @return integer with a unique id value
     */
    public int getID() {
        return fId;
    }

    /**
     * Returns the name of the Element
     *
     * @return name of the Element
     */
    public String getName() {
        return fName;
    }

    /**
     * Returns the X coördinate of the element
     *
     * @return x-coördinate
     */
    public int getX() {
        return fX;
    }

    /**
     * Returns the Y coördinate of the element
     *
     * @return y-coördinate
     */
    public int getY() {
        return fY;
    }

    /**
     * Sets the coördinates of the element
     *
     * @param x x-coördinate
     * @param y y-coördinate
     */
    public void setPosition(final int x, final int y) {
        fX = x;
        fY = y;
    }

    /**
     * Checks if this element is valid
     *
     * @return string with the reason why this element is not OK
     */
    abstract public String check();

    /**
     * Returns if this element is weak or not
     *
     * @return true if the element is weak
     */
    abstract public boolean isWeak();

    /**
     * Writes the XML rule for this element to the write buffer
     *
     * @param out Place to write to
     * @throws IOException
     */
    abstract public void write(final Writer out) throws IOException;

    /**
     * Writes the XML rule for a reference (id) to this element to the write buffer
     *
     * @param out Place to write to
     * @throws IOException
     */
    abstract public void writeReference(final Writer out) throws IOException;

}
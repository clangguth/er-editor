package Exceptions;

/**
 * <p>Title: LineException</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 17/03/2003
 */
public class LineException extends Exception {

    /**
     * Constructor
     *
     * @param s Error message
     */
    public LineException(final String s) {
        super(s);
    }

}
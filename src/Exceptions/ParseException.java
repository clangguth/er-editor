package Exceptions;

/**
 * <p>Title: ParseException</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 08/05/2003
 */
public class ParseException extends Exception {

    /**
     * Constructor
     *
     * @param s Error message
     */
    public ParseException(final String s) {
        super(s);
    }

}
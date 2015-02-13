package Exceptions;

/**
 * <p>Title: RoleException</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 11/12/2002
 */
public class RoleException extends Exception {

    /**
     * Constructor
     *
     * @param s Error message
     */
    public RoleException(final String s) {
        super(s);
    }

}
package Exceptions;

/**
 * <p>Title: FileException</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 21/06/2003
 */
public class FileException extends Exception {

    /**
     * Constructor
     *
     * @param s Error message
     */
    public FileException(final String s) {
        super(s);
    }

}
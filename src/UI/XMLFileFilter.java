package UI;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * <p>Title: XMLFileFilter</p>
 * <p>Description: Filter for XML files (used in file save/open box) </p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 03/04/2003
 */
public class XMLFileFilter extends FileFilter {

    /**
     * Returns true if should accept a given file
     *
     * @param f File
     * @return boolean
     */
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        String path = f.getPath().toLowerCase();
        if (path.endsWith(".xml")) return true;
        return false;
    }

    /**
     * File Description
     *
     * @return XML file description
     */
    public String getDescription() {
        return "XML Files (*.xml)";
    }

}

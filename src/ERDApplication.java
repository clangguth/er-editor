import UI.EREditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <p>Title: ERDApplication</p>
 * <p>Description: Main Editor class</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 27/04/2003
 */
public class ERDApplication {

    /**
     * Constructor
     */
    public ERDApplication() {
        final EREditor frame = new EREditor();

    /* Listener on the closing button of the main frame */
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.exit();
            }
        });

        frame.show();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    /**
     * Main function
     *
     * @param args Command line parameters
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new ERDApplication();
    }

}
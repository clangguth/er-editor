package UI.Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Title: SQLPanel</p>
 * <p>Description: A Panel with the SQL code for the active diagram</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 22/06/2003
 */
public class SQLPanel extends JDialog {

    /* Text Field which contains the SQL code */
    private JTextArea fTextArea = new JTextArea();

    /* Copy Button to copy the code to the system clipboard */
    private JButton fCopyButton = new JButton("Copy");

    /**
     * Constructor
     *
     * @param sql SQL code
     */
    public SQLPanel(final String sql) {
    /* Initialise GUI */
        setModal(true);
        JScrollPane scrollPane = new JScrollPane(fTextArea);
        getContentPane().add(scrollPane);

        fTextArea.setText(sql);

        setSize(600, 300);
        setTitle("SQL to Create a Database");

        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
        getContentPane().add(fCopyButton, BorderLayout.SOUTH);

    /* Listener on the 'Copy Button' */
        fCopyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                String s = fTextArea.getText();
                StringSelection contents = new StringSelection(s);
                cb.setContents(contents, null);
            }
        });
    }

}
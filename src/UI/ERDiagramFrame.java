package UI;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.*;
import java.io.File;

/**
 * <p>Title: ERDiagram</p>
 * <p>Description: A class representing an internal Entity Relationship Diagram</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 15/06/2003
 */
public class ERDiagramFrame extends JInternalFrame implements InternalFrameListener {

    /* Diagram that belongs to this diagram frame */
    private ERDiagram fDiagram;

    /* Editor to which this diagram frame belongs */
    private EREditor fEditor;

    /* File of the diagram */
    private File fThisFile;

    /* Indication if the file has been saved */
    private boolean fIsSaved = false;

    /* Counter for diagrams maded so far */
    static int fFramesOpen = 0;

    /* Counter for open diagrams */
    static int fOpenFrameCount = 0;

    /* Offsets to place multiple diagrams */
    static final int xOffset = 30, yOffset = 30;

    /* Scroll pane in this diagram frame */
    private JScrollPane fScrollPane;

    /**
     * Constructor
     *
     * @param editor Editor
     */
    public ERDiagramFrame(final EREditor editor) {
        super("Diagram #" + (++fOpenFrameCount), true, true, true, true);
        setSize(600, 500);
        fFramesOpen++;

    /* Initialise drawing area */
        fDiagram = new ERDiagram(editor, this);
        fEditor = editor;
        fDiagram.setBackground(Color.white);

    /* Scroll Pane */
        fScrollPane = new JScrollPane(fDiagram);
        fScrollPane.setPreferredSize(new Dimension(600, 500));
        getContentPane().add(fScrollPane);

    /* Set the window's location */
        setLocation(xOffset * fFramesOpen, yOffset * fFramesOpen);

        addInternalFrameListener(this);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    /**
     * Assign a file to this ER diagram
     *
     * @param file File
     */
    public void setFile(final File file) {
        fThisFile = file;
    }

    /**
     * Report a save of the diagram
     *
     * @param saved True if a save took place
     */
    public void setSaved(final boolean saved) {
        fIsSaved = saved;
    }

    /**
     * Returns if the file has been saved
     *
     * @return Saved
     */
    public boolean isSaved() {
        return fIsSaved;
    }

    /**
     * Returns the frame title
     *
     * @return Frame name
     */
    public String getName() {
        if (fThisFile == null) {
            return "Diagram #" + fOpenFrameCount;
        } else {
            return fThisFile.getName();
        }
    }

    /**
     * Returns the file that belongs the this diagram
     *
     * @return File
     */
    public File getFile() {
        return fThisFile;
    }

    /**
     * Sets the diagram of this frame
     *
     * @param diagram Diagram
     */
    public void setDiagram(final ERDiagram diagram) {
        fDiagram = diagram;
    }

    /**
     * Closing of this (internal) frame
     *
     * @param e Event
     */
    public void internalFrameClosing(final InternalFrameEvent e) {
        fEditor.setCurrentDiagram(this);
        fEditor.closeDiagram();
    }

    /**
     * Listener on closing the frame
     *
     * @param e Frame event
     */
    public void internalFrameClosed(final InternalFrameEvent e) {
    }

    /**
     * Listener on opening the frame
     *
     * @param e Frame event
     */
    public void internalFrameOpened(final InternalFrameEvent e) {
    }

    /**
     * Listener on minimalising the frame
     *
     * @param e Frame event
     */
    public void internalFrameIconified(final InternalFrameEvent e) {
    }

    /**
     * Listener on maximalising the frame
     *
     * @param e Frame event
     */
    public void internalFrameDeiconified(final InternalFrameEvent e) {
    }

    /**
     * Listener on deactivation of the frame
     *
     * @param e Frame event
     */
    public void internalFrameDeactivated(final InternalFrameEvent e) {
    }

    /**
     * Listener on activation of the frame
     *
     * @param e Frame event
     */
    public void internalFrameActivated(final InternalFrameEvent e) {
        fEditor.setCurrentDiagram(this);
    }

    /**
     * Returns the diagram that belongs to this frame
     *
     * @return Diagram
     */
    public ERDiagram getDiagram() {
        return fDiagram;
    }

}
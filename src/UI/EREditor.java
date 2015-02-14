package UI;

import Exceptions.FileException;
import Export.GifEncoder;
import UI.Panels.ElementPanel;
import UI.Panels.MapPanel;
import UI.Panels.SQLPanel;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * <p>Title: EREditor</p>
 * <p>Description: Main program class (main frame)</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 20/06/2003
 */
public class EREditor extends JFrame {

    /* Undo objects */
    public UndoManager fUndoManager; /* History list */
    public UndoableEditSupport fUndoSupport; /* Event support */

    /* GUI objects */
    private EREditor fEditor = this; /* Editor */
    private JDesktopPane fDesktopPane; /* Desktop (contains internal frame) */
    private JToolBar toolbar; /* Tool Bar */
    private JScrollPane fInfoScrollPane; /* Scroll Pane */
    private StatusBar fStatusBar; /* Status Bar */
    private ButtonGroup fDrawButtonGroup;
    private JButton[] buttons; /* Tool Bar buttons */
    private JToggleButton[] drawButtons; /* Tool Bar buttons */
    private JMenuItem[] fileActions, editActions, drawActions, extraActions, helpActions; /* Menu */
    private ElementPanel fCurrentPanel; /* Property panel */

    /* Current ER Diagram */
    private ERDiagramFrame fCurrentDiagram;

    /* Panels for file saving/opening */
    private JFileChooser fChooser, fGifChooser;

    /* Drawing options */
    private int fCurrentDrawObject = Constants.kEdit;
    private boolean fIntelligentDraw = true;
    private boolean fSmoothDraw = true;
    private boolean fColoredDraw = true;


    /* Load an icon from a resource or file name.
     */
    private ImageIcon loadIcon(String name) {
        return new ImageIcon(this.getClass().getResource("/" + name));
    }

    /**
     * Constructor
     */
    public EREditor() {
        super("ER-Editor 2.0");
        setLocation(200, 200);
        setIconImage(loadIcon("Images/icon.gif").getImage());
        getContentPane().setLayout(new BorderLayout());

    /* Set up the file boxes (for saving, exporting, opening) */
        fChooser = new JFileChooser();
        fGifChooser = new JFileChooser();
        fChooser.setFileFilter(new XMLFileFilter());
        fGifChooser.setFileFilter(new GIFFileFilter());

        frameInit();
        setCurrentDiagram(null);

    /* Set up the undo/redo manager */
        fUndoManager = new UndoManager();
        fUndoSupport = new UndoableEditSupport();
        fUndoSupport.addUndoableEditListener(new UndoAdapter());
        refreshUndoRedo();
    }

    /**
     * Initialise the main editor frame (toolbars, menubars, statusbar, ...)
     */
    protected void frameInit() {
        super.frameInit();

    /* File Menu */
        fileActions = new JMenuItem[]{
                new JMenuItem(new FileNewAction()),
                new JMenuItem(new FileOpenAction()),
                new JMenuItem(new FileSaveAction()),
                new JMenuItem(new FileSaveAsAction()),
                new JMenuItem(new FileCloseAction()),
                new JMenuItem(new FileCopyToClipboardAction()),
                new JMenuItem(new FileExportAction()),
                new JMenuItem(new FileExitAction())
        };

    /* Shortkeys */
        fileActions[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        fileActions[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        fileActions[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        fileActions[5].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        fileActions[6].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));

        JMenu fileMenu = new JMenu("File");
        for (int i = 0; i < fileActions.length; ++i) {
            fileMenu.add(fileActions[i]);
            if (i == 4 || i == 6) fileMenu.addSeparator();
        }

    /* Edit Menu */
        editActions = new JMenuItem[]{
                new JMenuItem(new EditUndoAction()),
                new JMenuItem(new EditRedoAction()),
                new JMenuItem(new EditDeleteAction()),
                new JMenuItem(new EditSelectAllAction()),
                new JMenuItem(new EditDeselectAllAction()),
                new JMenuItem(new EditAdjustFrameSizeAction()),
        };

    /* Shortkeys */
        editActions[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        editActions[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        editActions[3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        editActions[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));

        JMenu editMenu = new JMenu("Edit");
        for (int i = 0; i < editActions.length; ++i) {
            editMenu.add(editActions[i]);
            if (i == 1 || i == 2 || i == 4) editMenu.addSeparator();
        }

    /* Draw Menu */
        drawActions = new JMenuItem[]{
                new JMenuItem(new DrawEditAction()),
                new JMenuItem(new DrawEntityAction()),
                new JMenuItem(new DrawAttributeAction()),
                new JMenuItem(new DrawRelationshipAction()),
                new JMenuItem(new DrawISAAction()),
                new JMenuItem(new DrawLineAction())
        };

    /* Shortkeys */
        drawActions[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK));
        drawActions[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_DOWN_MASK));
        drawActions[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.CTRL_DOWN_MASK));
        drawActions[3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.CTRL_DOWN_MASK));
        drawActions[4].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, KeyEvent.CTRL_DOWN_MASK));
        drawActions[5].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, KeyEvent.CTRL_DOWN_MASK));

        JMenu drawMenu = new JMenu("Draw");
        for (int i = 0; i < drawActions.length; ++i) {
            drawMenu.add(drawActions[i]);
        }

    /* Extra Menu */
        extraActions = new JMenuItem[]{
                new JMenuItem(new ExtraCheckAction()),
                new JMenuItem(new ExtraMapAction()),
                new JMenuItem(new ExtraSQLAction()),
                new JCheckBoxMenuItem(new ExtraIntelligentDraw()),
                new JCheckBoxMenuItem(new ExtraColoredDraw()),
                new JCheckBoxMenuItem(new ExtraSmoothDraw())
        };

        JMenu extraMenu = new JMenu("Extra");
        for (int i = 0; i < extraActions.length; ++i) {
            extraMenu.add(extraActions[i]);
            if (i == 2) extraMenu.addSeparator();
        }

        ((JCheckBoxMenuItem) extraActions[3]).setState(fIntelligentDraw);
        ((JCheckBoxMenuItem) extraActions[4]).setState(fColoredDraw);
        ((JCheckBoxMenuItem) extraActions[5]).setState(fSmoothDraw);

    /* Help Menu */
        helpActions = new JMenuItem[]{
                new JMenuItem(new HelpHelpAction()),
                new JMenuItem(new HelpAboutAction())
        };

    /* Shortkeys */
        helpActions[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

        JMenu helpMenu = new JMenu("Help");
        for (int i = 0; i < helpActions.length; ++i) {
            helpMenu.add(helpActions[i]);
        }

    /* Menu Bar */
        JMenuBar menubar = new JMenuBar();
        menubar.add(fileMenu);
        menubar.add(editMenu);
        menubar.add(drawMenu);
        menubar.add(extraMenu);
        menubar.add(helpMenu);

    /* Tool Bar */
        toolbar = new JToolBar();
        toolbar.setFloatable(false); /* Cannot be moved */
        toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);

        buttons = new JButton[]{
                new JButton(fileActions[0].getAction()),
                new JButton(fileActions[1].getAction()),
                new JButton(fileActions[2].getAction()),
                new JButton(extraActions[0].getAction()),
                new JButton(extraActions[1].getAction()),
                new JButton(extraActions[2].getAction())
        };

        for (int i = 0; i < 3; ++i) {
            buttons[i].setText("");
            toolbar.add(buttons[i]);
        }

        buttons[0].setToolTipText("New diagram");
        buttons[1].setToolTipText("Open a diagram");
        buttons[2].setToolTipText("Save this diagram");
        buttons[3].setToolTipText("Check diagram for errors");
        buttons[4].setToolTipText("Map diagram to relational model");
        buttons[5].setToolTipText("Generate SQL code");

        drawButtons = new JToggleButton[]{
                new JToggleButton(drawActions[0].getAction()),
                new JToggleButton(drawActions[1].getAction()),
                new JToggleButton(drawActions[2].getAction()),
                new JToggleButton(drawActions[3].getAction()),
                new JToggleButton(drawActions[4].getAction()),
                new JToggleButton(drawActions[5].getAction()),
        };

        toolbar.addSeparator();
        fDrawButtonGroup = new ButtonGroup();
        for (int i = 0; i < drawButtons.length; ++i) {
            drawButtons[i].setText("");
            fDrawButtonGroup.add(drawButtons[i]);
            toolbar.add(drawButtons[i]);
        }

        drawButtons[0].setToolTipText("Edit");
        drawButtons[1].setToolTipText("Draw an entity");
        drawButtons[2].setToolTipText("Draw an attribute");
        drawButtons[3].setToolTipText("Draw a relationship");
        drawButtons[4].setToolTipText("Draw an ISA relationship");
        drawButtons[5].setToolTipText("Draw a line");

        toolbar.addSeparator();
        for (int i = 3; i < buttons.length; ++i) {
            buttons[i].setText("");
            toolbar.add(buttons[i]);
        }

        Container contentPane = getContentPane();
        contentPane.add(toolbar, BorderLayout.NORTH);
        setJMenuBar(menubar);

        fDesktopPane = new JDesktopPane();
        Dimension windowSize = getToolkit().getScreenSize();
        fDesktopPane.setPreferredSize(new Dimension(windowSize.width - 250, windowSize.height));

        fInfoScrollPane = new JScrollPane();

    /* Scheiding tussen beide delen (desktop en panel) */
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fDesktopPane, fInfoScrollPane);
        splitPane.setDividerSize(6);
        contentPane.add(splitPane);

    /* Status bar */
        fStatusBar = new StatusBar("Welcome to the ER Editor...");
        contentPane.add(fStatusBar, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Returns the selected draw element (line, entity, ...)
     *
     * @return selected draw element
     */
    public int getCurrentDrawObject() {
        return fCurrentDrawObject;
    }

    /**
     * Returns if the 'Intelligent Drawing' option is on/off
     *
     * @return boolean to indicate if the 'Intelligent Drawing' option is on/off
     */
    public boolean isIntelligentDrawing() {
        return fIntelligentDraw;
    }

    /**
     * Returns if the 'Smooth Drawing' option is on/off
     *
     * @return boolean to indicate if the 'Smooth Drawing' option is on/off
     */
    public boolean isSmoothDrawing() {
        return fSmoothDraw;
    }

    /**
     * Returns if the 'Colored Drawing' option is on/off
     *
     * @return boolean to indicate if the 'Colored Drawing' option is on/off
     */
    public boolean isColoredDrawing() {
        return fColoredDraw;
    }

    /**
     * Refresh the undo/redo menu (for instance: disable if there is nothing to undo)
     */
    private void refreshUndoRedo() {
    /* refresh undo */
        editActions[0].setEnabled(fUndoManager.canUndo());
        editActions[0].setText(fUndoManager.getUndoPresentationName());

    /* refresh redo */
        editActions[1].setEnabled(fUndoManager.canRedo());
        editActions[1].setText(fUndoManager.getRedoPresentationName());
    }

    /**
     * Shows the given property panel
     *
     * @param panel Property panel
     */
    public void showPanel(final ElementPanel panel) {
        fInfoScrollPane.setViewportView(panel);
        fInfoScrollPane.setVisible(true);
        fCurrentPanel = panel;
    }

    /**
     * Hides the property panel
     */
    public void hidePanel() {
        fInfoScrollPane.setViewport(null);
        if (fCurrentPanel != null) fCurrentPanel.removeFocus();
        fCurrentPanel = null;
    }

    /**
     * New file Action
     */
    class FileNewAction extends AbstractAction {
        public FileNewAction() {
            super("New...", loadIcon("Images/new.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            newDiagram();
        }
    }

    /**
     * Open file Action
     */
    class FileOpenAction extends AbstractAction {
        public FileOpenAction() {
            super("Open...", loadIcon("Images/open.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            openDiagram();
        }
    }

    /**
     * Save file Action
     */
    class FileSaveAction extends AbstractAction {
        public FileSaveAction() {
            super("Save", loadIcon("Images/save.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            try {
                saveDiagram();
            } catch (FileException e) {
                JOptionPane.showMessageDialog(fEditor, "Error while saving.", "Save Diagram", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Save file as Action
     */
    class FileSaveAsAction extends AbstractAction {
        public FileSaveAsAction() {
            super("Save As...", loadIcon("Images/save.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            saveDiagramAs();
        }
    }

    /**
     * Close file Action
     */
    class FileCloseAction extends AbstractAction {
        public FileCloseAction() {
            super("Close");
        }

        public void actionPerformed(ActionEvent event) {
            closeDiagram();
        }
    }

    /**
     * Export file Action
     */
    class FileExportAction extends AbstractAction {
        public FileExportAction() {
            super("Export To GIF...");
        }

        public void actionPerformed(ActionEvent event) {
            if (fEditor.isColoredDrawing() && fEditor.isSmoothDrawing()) {
                String err = "Unable to export to GIF when both 'Smooth Drawing' \nand 'Colored Drawing' are turned on";
                JOptionPane.showMessageDialog(fEditor, err, "Export to GIF", JOptionPane.ERROR_MESSAGE);
            } else {
                exportDiagram();
            }
        }
    }

    /**
     * Copy to Clipboard Action
     */
    class FileCopyToClipboardAction extends AbstractAction {
        public FileCopyToClipboardAction() {
            super("Copy to Clipboard...");
        }

        public void actionPerformed(ActionEvent event) {
            copyToClipboard();
        }
    }

    /**
     * Exit program Action
     */
    class FileExitAction extends AbstractAction {
        public FileExitAction() {
            super("Exit");
        }

        public void actionPerformed(ActionEvent event) {
            exit();
        }
    }

    /**
     * Undo Action
     */
    class EditUndoAction extends AbstractAction {
        public EditUndoAction() {
            super("Undo", loadIcon("Images/undo.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            fUndoManager.undo();
            refreshUndoRedo();
        }
    }

    /**
     * Redo Action
     */
    class EditRedoAction extends AbstractAction {
        public EditRedoAction() {
            super("Redo", loadIcon("Images/redo.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            fUndoManager.redo();
            refreshUndoRedo();
        }
    }

    /**
     * Delete Action
     */
    class EditDeleteAction extends AbstractAction {
        public EditDeleteAction() {
            super("Delete", loadIcon("Images/delete.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            fCurrentDiagram.getDiagram().deleteWithUndo();
            fCurrentDiagram.repaint();
        }
    }

    /**
     * Select All Action
     */
    class EditSelectAllAction extends AbstractAction {
        public EditSelectAllAction() {
            super("Select All");
        }

        public void actionPerformed(ActionEvent event) {
            fCurrentDiagram.getDiagram().selectAll();
            fCurrentDiagram.repaint();
        }
    }

    /**
     * Deselect All Action
     */
    class EditDeselectAllAction extends AbstractAction {
        public EditDeselectAllAction() {
            super("Deselect All");
        }

        public void actionPerformed(ActionEvent event) {
            fCurrentDiagram.getDiagram().deselectAll();
            fCurrentDiagram.repaint();
        }
    }

    /**
     * Adjust frame size Action
     */
    class EditAdjustFrameSizeAction extends AbstractAction {
        public EditAdjustFrameSizeAction() {
            super("Adjust Frame Size");
        }

        public void actionPerformed(ActionEvent event) {
            fCurrentDiagram.getDiagram().adjustFrameSizeWithUndo();
            fCurrentDiagram.repaint();
        }
    }

    /**
     * Select 'edit' as drawing element
     */
    class DrawEditAction extends AbstractAction {
        public DrawEditAction() {
            super("Edit", loadIcon("Images/edit.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            drawButtons[0].setSelected(true);
            fCurrentDrawObject = Constants.kEdit;
            fCurrentDiagram.repaint();
        }
    }

    /**
     * Select 'entity' as drawing element
     */
    class DrawEntityAction extends AbstractAction {
        public DrawEntityAction() {
            super("Entity", loadIcon("Images/entity.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            drawButtons[1].setSelected(true);
            fCurrentDrawObject = Constants.kEntity;
            fCurrentDiagram.repaint();
        }
    }

    /**
     * Select 'attribute' as drawing element
     */
    class DrawAttributeAction extends AbstractAction {
        public DrawAttributeAction() {
            super("Attribute", loadIcon("Images/attribute.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            drawButtons[2].setSelected(true);
            fCurrentDrawObject = Constants.kAttribute;
            fCurrentDiagram.repaint();
        }
    }

    /**
     * Select 'relationship' as drawing element
     */
    class DrawRelationshipAction extends AbstractAction {
        public DrawRelationshipAction() {
            super("Relationship", loadIcon("Images/relationship.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            drawButtons[3].setSelected(true);
            fCurrentDrawObject = Constants.kRelationship;
            fCurrentDiagram.repaint();
        }
    }

    /**
     * Select 'ISA relationship' as drawing element
     */
    class DrawISAAction extends AbstractAction {
        public DrawISAAction() {
            super("ISA Relationship", loadIcon("Images/isa.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            drawButtons[4].setSelected(true);
            fCurrentDrawObject = Constants.kISA;
            fCurrentDiagram.repaint();
        }
    }

    /**
     * Select 'line' as drawing element
     */
    class DrawLineAction extends AbstractAction {
        public DrawLineAction() {
            super("Line", loadIcon("Images/line.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            drawButtons[5].setSelected(true);
            fCurrentDrawObject = Constants.kLine;
        }
    }

    /**
     * Select 'edit' as drawing element
     */
    class ExtraCheckAction extends AbstractAction {
        public ExtraCheckAction() {
            super("Check Model", loadIcon("Images/check.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            String reason = fCurrentDiagram.getDiagram().check();
            if (reason != "") {
                JOptionPane.showMessageDialog(null, reason, "Checking ER Diagram", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No errors found.", "Checking ER Diagram", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Map to relational model Action
     */
    class ExtraMapAction extends AbstractAction {
        public ExtraMapAction() {
            super("Map Model...", loadIcon("Images/map.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            String reason = fCurrentDiagram.getDiagram().check();
            if (reason != "") {
        /* There are errors, so the diagram cannot be mapped */
                JOptionPane.showMessageDialog(fEditor, reason, "Checking ER Diagram", JOptionPane.ERROR_MESSAGE);
            } else {
                MapPanel mapPanel = new MapPanel();
                mapPanel.print(fCurrentDiagram.getDiagram().getMapping());
                mapPanel.show();
            }
        }
    }

    /**
     * Generate SQL Code Action
     */
    class ExtraSQLAction extends AbstractAction {
        public ExtraSQLAction() {
            super("Generate SQL Code...", loadIcon("Images/sql.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            String reason = fCurrentDiagram.getDiagram().check();
            if (reason != "") {
        /* There are errors, so no SQL code can be generated */
                JOptionPane.showMessageDialog(fEditor, reason, "Checking ER Diagram", JOptionPane.ERROR_MESSAGE);
            } else {
                String sql = fCurrentDiagram.getDiagram().getSQL();
                SQLPanel sqlPanel = new SQLPanel(sql);
                sqlPanel.show();
            }
        }
    }

    /**
     * Turn on/off the Intelligent Drawing mode
     */
    class ExtraIntelligentDraw extends AbstractAction {
        public ExtraIntelligentDraw() {
            super("Intelligent Drawing");
        }

        public void actionPerformed(ActionEvent event) {
            fIntelligentDraw = !fIntelligentDraw;
        }
    }

    /**
     * Turn on/off the Colored Drawing mode
     */
    class ExtraColoredDraw extends AbstractAction {
        public ExtraColoredDraw() {
            super("Colored Drawing");
        }

        public void actionPerformed(ActionEvent event) {
            fColoredDraw = !fColoredDraw;
            if (fCurrentDiagram != null) fCurrentDiagram.repaint();
        }
    }

    /**
     * Turn on/off the Smooth Drawing mode
     */
    class ExtraSmoothDraw extends AbstractAction {
        public ExtraSmoothDraw() {
            super("Smooth Drawing");
        }

        public void actionPerformed(ActionEvent event) {
            fSmoothDraw = !fSmoothDraw;
            if (fCurrentDiagram != null) fCurrentDiagram.repaint();
        }
    }


    /**
     * Help Action
     */
    class HelpHelpAction extends AbstractAction {
        public HelpHelpAction() {
            super("Help...", loadIcon("Images/help.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            String msg = "Look for the help PDF file in the directory of this program.";
            JOptionPane.showMessageDialog((Component) event.getSource(), msg, "ER-Editor Help", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Help About Action
     */
    class HelpAboutAction extends AbstractAction {
        public HelpAboutAction() {
            super("About...", loadIcon("Images/about.gif"));
        }

        public void actionPerformed(ActionEvent event) {
            String msg = "ER-Editor Copyright 2002-2003\nAuthor: Gert Helsen (gerthelsen@pandora.be)";
            JOptionPane.showMessageDialog((Component) event.getSource(), msg, "About ER-Editor: version 2.0", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Exit Program
     */
    public void exit() {
        JInternalFrame[] diagramFrames = fDesktopPane.getAllFrames();
        for (int i = 0; i < diagramFrames.length; i++) {
      /* Close all the active diagrams */
            fCurrentDiagram = (ERDiagramFrame) diagramFrames[i];
            if (closeDiagram() != 1) return;
        }
    /* Exit program */
        dispose();
        System.exit(0);
    }

    /**
     * Close active diagram
     *
     * @return 1 if succes, 0 if not
     */
    protected int closeDiagram() {
        if (fCurrentDiagram != null) {
            if (!fCurrentDiagram.getDiagram().isUnedited()) {
                int n = JOptionPane.showConfirmDialog(this, "Would you like to save changes ?", "Close " + fCurrentDiagram.getTitle(), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                switch (n) {
                    case JOptionPane.YES_OPTION:
            /* Save diagram */
                        try {
                            saveDiagram();
                        } catch (FileException e) {
                            JOptionPane.showMessageDialog(fEditor, e.getMessage(), "Save Diagram", JOptionPane.ERROR_MESSAGE);
                            return 0;
                        }
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        return 0;
                    case JOptionPane.CLOSED_OPTION:
                        return 0;
                }
            }
      /* Close diagram frame */
            fDesktopPane.getDesktopManager().closeFrame(fCurrentDiagram);
            ERDiagramFrame.fFramesOpen--;
            fDesktopPane.revalidate();
            fDesktopPane.repaint();
            if (fDesktopPane.getSelectedFrame() == null) {
        /* No diagrams anymore */
                fCurrentDiagram = null;
                fUndoManager.discardAllEdits();
                refreshUndoRedo();
                hidePanel();
            } else {
        /* Activate next diagram */
                fCurrentDiagram = (ERDiagramFrame) fDesktopPane.getSelectedFrame();
                hidePanel();
            }

            updateMenuVisibility();
        }
        return 1;
    }

    /**
     * Update the menu / toolbar buttons (hide them when there no diagram is open)
     */
    public void updateMenuVisibility() {
        boolean setVisible = (fCurrentDiagram != null);
    /* Menu */
        for (int i = 2; i < 7; ++i) {
            fileActions[i].setEnabled(setVisible);
        }
        for (int i = 2; i < 6; ++i) {
            editActions[i].setEnabled(setVisible);
        }
        for (int i = 0; i < drawActions.length; ++i) {
            drawActions[i].setEnabled(setVisible);
        }
        for (int i = 0; i < 3; ++i) {
            extraActions[i].setEnabled(setVisible);
        }
    /* Buttons */
        for (int i = 2; i < buttons.length; ++i) {
            buttons[i].setEnabled(setVisible);
        }
        for (int i = 0; i < drawButtons.length; ++i) {
            drawButtons[i].setEnabled(setVisible);
        }
    }

    /**
     * Set the current diagram
     *
     * @param diagramFrame New current diagram
     */
    protected void setCurrentDiagram(final ERDiagramFrame diagramFrame) {
        fCurrentDiagram = diagramFrame;
        updateMenuVisibility();
    }

    /**
     * Open a new diagram frame
     */
    protected void newDiagram() {
        ERDiagramFrame diagramFrame = new ERDiagramFrame(this);
        fDesktopPane.add(diagramFrame, JLayeredPane.DEFAULT_LAYER);
        diagramFrame.setVisible(true);
    }

    /**
     * Set a given message for the status bar
     *
     * @param message Message to set
     */
    public void setStatusMessage(String message) {
        fStatusBar.setStatus(message);
    }

    /**
     * Open a diagram (from file)
     */
    protected void openDiagram() {
        fChooser.setSelectedFile(null);
        int n = fChooser.showOpenDialog(this);
        if (n == 0) {
            File file = fChooser.getSelectedFile();
            try {
                newDiagram();
        /* Parse the file */
                Parser parser = new Parser(fCurrentDiagram.getDiagram());
                parser.run(file);
                fCurrentDiagram.setFile(file);
                fCurrentDiagram.getDiagram().setUnedited(true);
                fCurrentDiagram.setSaved(true);
                fCurrentDiagram.getDiagram().adjustFrameSize();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Open an XML File", JOptionPane.ERROR_MESSAGE);
            }
        }
        repaint();
    }

    /**
     * Save the current diagram
     */
    protected synchronized void saveDiagramAs() {
        fChooser.setSelectedFile(null);
        int n = fChooser.showSaveDialog(this);
        if (n == 0) {
            String fileName = fChooser.getSelectedFile().getName();
            File file = new File(fChooser.getCurrentDirectory(), fileName);

            String filePath = file.getPath();
            if ((!filePath.endsWith(".xml")) && (!filePath.endsWith(".XML"))) {
        /* Add file extension */
                filePath += ".xml";
                file = new File(filePath);
            }

            if (file.exists()) {
        /* File already exists */
                if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(this, file.getName() + " already exists. Overwrite ?", "Confirm Save As", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
                    return;
                }
            }

            try {
        /* Save diagram to the specified file */
                fCurrentDiagram.setSaved(true);
                fCurrentDiagram.setFile(file);
                saveDiagram();
            } catch (FileException e) {
        /* Illegal file, try again */
                JOptionPane.showMessageDialog(fEditor, e.getMessage(), "Save Diagram", JOptionPane.ERROR_MESSAGE);
                fCurrentDiagram.setFile(null);
                fCurrentDiagram.setSaved(false);
                saveDiagramAs();
                return;
            }
        }
        repaint();
    }

    /**
     * Export current diagram to GIF
     */
    protected void exportDiagram() {
        int n = fGifChooser.showSaveDialog(this);
        if (n == 0) {
            String fileName = fGifChooser.getSelectedFile().getName();
            File file = new File(fGifChooser.getCurrentDirectory(), fileName);

            String filePath = file.getPath();
            if ((!filePath.endsWith(".gif")) && (!filePath.endsWith(".GIF"))) {
        /* Add file extension */
                filePath += ".gif";
                file = new File(filePath);
            }
            try {
        /* Save GIF file */
                FileOutputStream out = new FileOutputStream(file);
                Image image = fCurrentDiagram.getDiagram().getImage();
                new GifEncoder(image, out).encode();
                out.flush();
                out.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(fEditor, "Illegal File. GIF can not be saved.", "Export to GIF", JOptionPane.ERROR_MESSAGE);
                exportDiagram();
            }
        }
    }

    /**
     * Copy the current diagram to the clipboard
     */
    protected void copyToClipboard() {
        Image image = fCurrentDiagram.getDiagram().getImage();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        ImageSelection imgSel = new ImageSelection(image);
        clipboard.setContents(imgSel, null);
    }

    /**
     * Save the active diagram (to file)
     *
     * @throws FileException Illegal File. Diagram can not be saved
     */
    protected synchronized void saveDiagram() throws FileException {
        if (fCurrentDiagram.isSaved()) {
            try {
        /* Save XML file */
                FileOutputStream out = new FileOutputStream(fCurrentDiagram.getFile());
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(out));
                fCurrentDiagram.getDiagram().write(output);
                output.flush();
                output.close();
            } catch (Exception e) {
                throw new FileException("Illegal File. Diagram can not be saved.");
            }
            fCurrentDiagram.getDiagram().setUnedited(true);
        } else saveDiagramAs();
    }

    /**
     * Class for a status bar
     */
    public class StatusBar extends JPanel {

        /* Label to display the status message */
        JLabel fLblStatus = null;

        /**
         * Constructor
         *
         * @param status Begin status text
         */
        public StatusBar(final String status) {
            super(new BorderLayout(0, 0));
            fLblStatus = new JLabel(status);
            fLblStatus.setBorder(BorderFactory.createLoweredBevelBorder());
            add(fLblStatus, "Center");
        }

        /**
         * Set the Status
         *
         * @param text Status
         */
        public void setStatus(final String text) {
            fLblStatus.setText(text);
        }
    }

    /**
     * This class is used to hold an image while on the clipboard.
     */
    public static class ImageSelection implements Transferable {

        /* Image of the diagram */
        private Image fImage;

        /**
         * Constructor
         *
         * @param image Image of the diagram
         */
        public ImageSelection(Image image) {
            fImage = image;
        }

        /**
         * Returns supported flavors
         *
         * @return flavors
         */
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        /**
         * Returns true if flavor is supported
         *
         * @param flavor Flavor
         * @return boolean to indicate if flavor is supported
         */
        public boolean isDataFlavorSupported(final DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        /**
         * Returns image
         *
         * @param flavor Flavor
         * @return Image
         * @throws UnsupportedFlavorException
         * @throws IOException
         */
        public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return fImage;
        }
    }

    /**
     * An undo/redo adpater. The adpater is notified when an undo edit occur(e.g. add or remove from the list)
     * The adptor extract the edit from the event, add it to the UndoManager, and refresh the GUI
     */
    private class UndoAdapter implements UndoableEditListener {
        public void undoableEditHappened(final UndoableEditEvent evt) {
            UndoableEdit edit = evt.getEdit();
            fUndoManager.addEdit(edit);
            refreshUndoRedo();
        }
    }

}
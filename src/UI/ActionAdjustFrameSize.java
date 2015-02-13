package UI;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * <p>Title: ActionAdjustFrameSize</p>
 * <p>Description: Class to undo / redo an AjustFrameSize action</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 15/06/2003
 */
public class ActionAdjustFrameSize extends AbstractUndoableEdit {

    /* Diagram on which this action took place */
    private ERDiagram fDiagram;

    /* Previous frame size */
    private int fMaxX, fMaxY;

    /**
     * Constructor
     * Gather information to allow an undo
     *
     * @param diagram Diagram where the action takes place
     * @param maxX    Previous X frame coördinate
     * @param maxY    Previous Y frame coördinate
     */
    public ActionAdjustFrameSize(final ERDiagram diagram, final int maxX, final int maxY) {
        diagram.getEditor().setStatusMessage(" ");
        fDiagram = diagram;
        fMaxX = maxX;
        fMaxY = maxY;
        fDiagram.setUnedited(false);
    }

    /**
     * Undo the AdjustFrameSize action
     *
     * @throws CannotUndoException
     */
    public void undo() throws CannotUndoException {
        fDiagram.updateFrameSize(fMaxX, fMaxY);
        fDiagram.repaint();
    }

    /**
     * Redo the AdjustFrameSize action
     *
     * @throws CannotRedoException
     */
    public void redo() throws CannotRedoException {
        fDiagram.adjustFrameSize();
    }

    /**
     * Returns true (undo is allowed)
     *
     * @return true
     */
    public boolean canUndo() {
        return true;
    }

    /**
     * Returns true (redo is allowed)
     *
     * @return true
     */
    public boolean canRedo() {
        return true;
    }

    /**
     * Returns the name to display in the undo list
     *
     * @return action name
     */
    public String getPresentationName() {
        return "Adjust Frame Size";
    }

}
package UI;

import Shapes.DrawableElement;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: ActionTranslate</p>
 * <p>Description: Class to undo / redo a Translation action</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 08/06/2003
 */
public class ActionTranslate extends AbstractUndoableEdit {

    /* Diagram on which this action took place */
    private ERDiagram fDiagram;

    /* Translated elements */
    private ArrayList fElements = new ArrayList();

    /* Previous coördinates */
    private int fX, fY;

    /* Unedited status before the add */
    private boolean fUnedited;

    /**
     * Constructor
     * Gather information to allow an undo
     *
     * @param diagram   Diagram where the action takes place
     * @param selection Elements to translate
     * @param x         Previous X-coordinate
     * @param y         Previous Y-coordinate
     */
    public ActionTranslate(final ERDiagram diagram, final ArrayList selection, final int x, final int y) {
        diagram.getEditor().setStatusMessage(" ");
        fDiagram = diagram;
        fElements.addAll(selection);
        fX = x;
        fY = y;
        fUnedited = fDiagram.isUnedited();
        fDiagram.setUnedited(false);
    }

    /**
     * Undo the translation action
     *
     * @throws CannotUndoException
     */
    public void undo() throws CannotUndoException {
        Iterator itSelection = fElements.iterator();
        while (itSelection.hasNext()) {
            DrawableElement element = (DrawableElement) itSelection.next();
            element.translate(-fX, -fY);
        }
        fDiagram.setUnedited(fUnedited);
        fDiagram.repaint();
    }

    /**
     * Redo the translation action
     *
     * @throws CannotRedoException
     */
    public void redo() throws CannotRedoException {
        Iterator itSelection = fElements.iterator();
        while (itSelection.hasNext()) {
            DrawableElement element = (DrawableElement) itSelection.next();
            element.translate(fX, fY);
        }
        fDiagram.setUnedited(false);
        fDiagram.repaint();
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
        return "Translation";
    }

}
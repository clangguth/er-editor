package UI;

import Reps.Role;
import Shapes.DrawableElement;
import Shapes.Line;

import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * <p>Title: ActionAdd</p>
 * <p>Description: Class to undo / redo an Add action</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 17/06/2003
 */
public class ActionAdd extends AbstractUndoableEdit {

    /* Diagram on which this action took place */
    private ERDiagram fDiagram;

    /* Added element */
    private DrawableElement fElement;

    /* Unedited status before the add */
    private boolean fUnedited;

    /**
     * Constructor
     * Gather information to allow an undo
     *
     * @param diagram Diagram where the action takes place
     * @param element Element to add
     */
    public ActionAdd(final ERDiagram diagram, final DrawableElement element) {
        diagram.getEditor().setStatusMessage(" ");
        fDiagram = diagram;
        fElement = element;
        fUnedited = fDiagram.isUnedited();
        fDiagram.setUnedited(false);
    }

    /**
     * Undo the add action
     *
     * @throws CannotUndoException
     */
    public void undo() throws CannotUndoException {
        fDiagram.deselect(fElement);
        fDiagram.delete(fElement);
        fDiagram.setUnedited(fUnedited);
        fDiagram.repaint();
    }

    /**
     * Redo the add action
     *
     * @throws CannotRedoException
     */
    public void redo() throws CannotRedoException {
        try {
            if (fElement instanceof Line) {
                Line line = (Line) fElement;
                line.getShape2().connect((Role) line.getRep(), line.getShape1());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unknown Error. Please report this to gerthelsen@pandora.be", "Redo Error", JOptionPane.ERROR_MESSAGE);
        }
        fDiagram.add(fElement);
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
        return "Add Element";
    }

}
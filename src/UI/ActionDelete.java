package UI;

import Reps.EntityRep;
import Reps.RelationshipRep;
import Reps.Role;
import Shapes.DrawableElement;
import Shapes.Entity;
import Shapes.Line;

import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Title: ActionDelete</p>
 * <p>Description: Class to undo / redo a Delete action</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 20/06/2003
 */
public class ActionDelete extends AbstractUndoableEdit {

    /* Diagram on which this action took place */
    private ERDiagram fDiagram;

    /* Deleted elements */
    private ArrayList fMainElements = new ArrayList();

    /* Deleted lines, connected to the elements */
    private ArrayList fConnectedLines = new ArrayList();

    /* Previous dependencies of the diagram */
    private HashMap fDependencies = new HashMap();

    /* Unedited status before the add */
    private boolean fUnedited;

    /**
     * Constructor
     * Gather information to allow an undo
     *
     * @param diagram   Diagram where the action takes place
     * @param selection Selected elements
     */
    public ActionDelete(final ERDiagram diagram, final ArrayList selection) {
        diagram.getEditor().setStatusMessage(" ");
        fDiagram = diagram;
        fUnedited = fDiagram.isUnedited();
        fDiagram.setUnedited(false);
        Iterator itSelection = selection.iterator();
        while (itSelection.hasNext()) {
      /* Main element */
            DrawableElement element = (DrawableElement) itSelection.next();
            fMainElements.add(element);
      /* Connected lines */
            Iterator itLines = element.getConnectedLinesWith().iterator();
            while (itLines.hasNext()) {
                Line line = (Line) itLines.next();
                if (!fConnectedLines.contains(line) && !fMainElements.contains(line)) fConnectedLines.add(line);
            }
        }

    /* Store previous dependencies to allow an undo */
        Iterator itAllElements = diagram.getElements().iterator();
        while (itAllElements.hasNext()) {
            DrawableElement element = (DrawableElement) itAllElements.next();
            if (element instanceof Entity) {
                EntityRep entity = (EntityRep) element.getRep();
                Iterator itDependencies = entity.getDependentRelationships().iterator();
                while (itDependencies.hasNext()) {
                    RelationshipRep relationship = (RelationshipRep) itDependencies.next();
                    if (!fDependencies.containsKey(relationship))
                        fDependencies.put(relationship, relationship.getWeakEntity());
                }
            }
        }
    }

    /**
     * Undo the delete action
     *
     * @throws CannotUndoException
     */
    public void undo() throws CannotUndoException {
        try {
      /* Re-add the elements */
            Iterator itSelection = fMainElements.iterator();
            while (itSelection.hasNext()) {
                DrawableElement element = (DrawableElement) itSelection.next();
                if (element instanceof Line) {
                    Line line = (Line) element;
                    fDiagram.add(line);
                    line.getShape2().connect((Role) line.getRep(), line.getShape1());
                } else {
                    fDiagram.add(element);
                }
            }

      /* Re-add the lines connecting these elements */
            Iterator itLines = fConnectedLines.iterator();
            while (itLines.hasNext()) {
                Line line = (Line) itLines.next();
                fDiagram.add(line);
                line.getShape2().connect((Role) line.getRep(), line.getShape1());
            }

      /* Re-add dependencies */
            Iterator itDependencies = fDependencies.keySet().iterator();
            while (itDependencies.hasNext()) {
                RelationshipRep relationship = (RelationshipRep) itDependencies.next();
                EntityRep weakEntity = (EntityRep) fDependencies.get(relationship);
                if (!weakEntity.hasDependency(relationship)) weakEntity.addDependency(relationship, true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unknown Error. Please report this to gerthelsen@pandora.be", "Redo Error", JOptionPane.ERROR_MESSAGE);
        }
        fDiagram.setUnedited(fUnedited);
        fDiagram.repaint();
    }

    /**
     * Redo the delete action
     *
     * @throws CannotRedoException
     */
    public void redo() throws CannotRedoException {
        Iterator itSelection = fMainElements.iterator();
        while (itSelection.hasNext()) {
            DrawableElement element = (DrawableElement) itSelection.next();
            fDiagram.delete(element);
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
        return "Delete Selection";
    }

}
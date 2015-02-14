package UI;

import Reps.*;
import Shapes.DrawableElement;

import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * <p>Title: ActionChangeProperty</p>
 * <p>Description: Class to undo / redo an ChangeProperty action</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 16/06/2003
 */
public class ActionChangeProperty extends AbstractUndoableEdit {

    /* Diagram on which this action took place */
    private ERDiagram fDiagram;

    /* Element of which the property has been change */
    private DrawableElement fElement;
    private Element fElementRep;

    /* Previous properties */
    private String fPropertyName;
    private String fOldValue;
    private String fNewValue;
    private RelationshipRep fRelationship;

    /* Unedited status before the property change */
    private boolean fUnedited;

    /**
     * Constructor
     * Gather information to allow an undo
     *
     * @param diagram      Diagram where the action takes place
     * @param element      Element of which the property will be changed
     * @param propertyName Property Name
     * @param oldValue     Old property value
     * @param newValue     New property value
     */
    public ActionChangeProperty(final ERDiagram diagram, final DrawableElement element, final String propertyName, final String oldValue, final String newValue) {
        diagram.getEditor().setStatusMessage(" ");
        fDiagram = diagram;
        fElement = element;
        fElementRep = element.getRep();
        fPropertyName = propertyName;
        fOldValue = oldValue;
        fNewValue = newValue;
        fUnedited = fDiagram.isUnedited();
        fDiagram.setUnedited(false);
    }

    public ActionChangeProperty(ERDiagram diagram, DrawableElement element, String propertyName, String oldValue, String newValue, EntityRep entity, RelationshipRep relationship) {
        diagram.getEditor().setStatusMessage(" ");
        fDiagram = diagram;
        fElement = element;
        fElementRep = entity;
        fPropertyName = propertyName;
        fOldValue = oldValue;
        fNewValue = newValue;
        fRelationship = relationship;
        fUnedited = fDiagram.isUnedited();
        fDiagram.setUnedited(false);
    }

    /**
     * Undo the Change Property action
     *
     * @throws CannotUndoException
     */
    public void undo() throws CannotUndoException {
        try {
            if (fPropertyName.equals("Name")) {
                fElementRep.setName(fOldValue);
                fElement.adjustWidthToName(fDiagram);
            } else if (fPropertyName.equals("Weak")) {
                if (Boolean.valueOf(fNewValue).booleanValue()) {
                    ((EntityRep) fElement.getRep()).removeDependency(fRelationship);
                } else {
                    ((EntityRep) fElement.getRep()).addDependency(fRelationship, false);
                }
            } else if (fPropertyName.equals("Type"))
                ((AttributeRep) fElementRep).setType(fOldValue);
            else if (fPropertyName.equals("Required"))
                ((AttributeRep) fElementRep).setRequired(Boolean.valueOf(fOldValue).booleanValue());
            else if (fPropertyName.equals("Unique"))
                ((AttributeRep) fElementRep).setUnique(Boolean.valueOf(fOldValue).booleanValue());
            else if (fPropertyName.equals("DataType"))
                ((AttributeRep) fElementRep).setDataType(fOldValue);
            else if (fPropertyName.equals("Length"))
                ((AttributeRep) fElementRep).setLength(Integer.valueOf(fOldValue).intValue());
            else if (fPropertyName.equals("MinCard"))
                ((Role) fElementRep).setMinCard(fOldValue);
            else if (fPropertyName.equals("MaxCard"))
                ((Role) fElementRep).setMaxCard(fOldValue);
            else if (fPropertyName.equals("RefIntegrity"))
                ((Role) fElementRep).setRefIntegrity(Boolean.valueOf(fOldValue).booleanValue(), false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unknown Error", "Undo Error", JOptionPane.ERROR_MESSAGE);
        }
        fDiagram.setUnedited(fUnedited);
        fDiagram.getEditor().repaint();
    }

    /**
     * Redo the Change Property action
     *
     * @throws CannotRedoException
     */
    public void redo() throws CannotRedoException {
        try {
            if (fPropertyName.equals("Name")) {
                fElementRep.setName(fNewValue);
                fElement.adjustWidthToName(fDiagram);
            } else if (fPropertyName.equals("Type")) ((AttributeRep) fElementRep).setType(fNewValue);
            else if (fPropertyName.equals("Weak")) {
                if (Boolean.valueOf(fNewValue).booleanValue()) {
                    ((EntityRep) fElement.getRep()).addDependency(fRelationship, false);
                } else {
                    ((EntityRep) fElement.getRep()).removeDependency(fRelationship);
                }
            } else if (fPropertyName.equals("Required"))
                ((AttributeRep) fElementRep).setRequired(Boolean.valueOf(fNewValue).booleanValue());
            else if (fPropertyName.equals("Unique"))
                ((AttributeRep) fElementRep).setUnique(Boolean.valueOf(fNewValue).booleanValue());
            else if (fPropertyName.equals("DataType"))
                ((AttributeRep) fElementRep).setDataType(fNewValue);
            else if (fPropertyName.equals("Length"))
                ((AttributeRep) fElementRep).setLength(Integer.valueOf(fNewValue).intValue());
            else if (fPropertyName.equals("MinCard"))
                ((Role) fElementRep).setMinCard(fNewValue);
            else if (fPropertyName.equals("MaxCard"))
                ((Role) fElementRep).setMaxCard(fNewValue);
            else if (fPropertyName.equals("RefIntegrity"))
                ((Role) fElementRep).setRefIntegrity(Boolean.valueOf(fNewValue).booleanValue(), false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unknown Error. Please report this to gerthelsen@pandora.be", "Redo Error", JOptionPane.ERROR_MESSAGE);
        }
        fDiagram.setUnedited(false);
        fDiagram.getEditor().repaint();
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
        return "Change " + fPropertyName;
    }

}
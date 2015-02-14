package UI.Panels;

import Reps.EntityRep;
import Reps.RelationshipRep;
import Reps.Role;
import Shapes.Entity;
import UI.ActionChangeProperty;
import UI.ERDiagram;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.UndoableEdit;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: EntityPanel</p>
 * <p>Description: A class representing the property panel for entities</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 19/06/2003
 */
public class EntityPanel extends ElementPanel {

    private WeakEntitiesTableModel fTableModel3;
    private BeginWeakEntityTableModel fTableModel2;
    private JTable fTable3;
    private JTable fTable2;

    public EntityPanel() {
        super();
        fTableModel2 = new BeginWeakEntityTableModel();
        fTableModel3 = new WeakEntitiesTableModel();
        fTable2 = new JTable(fTableModel2);
        fTable3 = new JTable(fTableModel3);
        fTable3.setRowSelectionAllowed(false);
        fTable3.setRowSelectionAllowed(false);
        add(fTable2);
        add(fTable3);
    }

    public void update(ERDiagram diagram, Entity entity) {
        super.update(diagram, entity);
        fTableModel2.setEntity(diagram, entity);
        fTableModel2.fireTableStructureChanged();
        fTableModel3.setEntity(diagram, entity);
        fTableModel3.fireTableStructureChanged();
    }

}

/**
 * Table Model to display the 'weak elements' title
 */
class BeginWeakEntityTableModel extends AbstractTableModel {

    private Entity fEntity;
    private EntityRep fEntityRep;
    private ERDiagram fDiagram;

    private ArrayList fPossibleWeakRelationships = new ArrayList();

    public Object getValueAt(int row, int col) {
        return "Weak Entities:";
    }

    public int getColumnCount() {
        return 1;
    }

    public int getRowCount() {
        if (!fPossibleWeakRelationships.isEmpty()) {
            return 1;
        } else return 0;
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void setEntity(ERDiagram diagram, Entity entity) {
        fEntity = entity;
        fEntityRep = (EntityRep) entity.getRep();
        fDiagram = diagram;
        setPossibleDependentRelationships();
    }

    private void setPossibleDependentRelationships() {
        fPossibleWeakRelationships.clear();
        Iterator itRelationships = fEntityRep.getConnectedRelationships().iterator();
        while (itRelationships.hasNext()) {
            RelationshipRep relationship = (RelationshipRep) itRelationships.next();
            if (fEntityRep.isWeak() || fEntityRep.canBeSetDependentOf(relationship)) {
                fPossibleWeakRelationships.add(relationship);
            }
        }
    }

}

/**
 * Table Model to display (possible) weak elements
 */
class WeakEntitiesTableModel extends AbstractTableModel {

    private EntityRep fEntityRep;
    private Entity fEntity;
    private ERDiagram fDiagram;
    private ArrayList fPossibleWeakRelationships = new ArrayList();

    public Object getValueAt(int row, int col) {
        RelationshipRep relationship = (RelationshipRep) fPossibleWeakRelationships.get(row);
        if (col == 0) {
            ArrayList roles = relationship.getRoles();
            EntityRep entity1 = ((Role) roles.get(0)).getEntity();
            EntityRep entity2 = ((Role) roles.get(1)).getEntity();
            if (entity1 != fEntityRep) {
                return entity1.getName();
            } else {
                return entity2.getName();
            }
        } else if (col == 1) return relationship.getName();
        else if (col == 2) {
            return new Boolean(relationship.isWeak());
        } else return "";
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        return fPossibleWeakRelationships.size();
    }

    public boolean isCellEditable(int row, int col) {
        return (col == 2);
    }

    public void setValueAt(Object value, int row, int col) {
        fireTableCellUpdated(row, col);
        fDiagram.setUnedited(false);
        RelationshipRep relationship = (RelationshipRep) fPossibleWeakRelationships.get(row);
        ArrayList roles = relationship.getRoles();
        EntityRep entity1 = ((Role) roles.get(0)).getEntity();
        EntityRep entity2 = ((Role) roles.get(1)).getEntity();
        try {
            if ((col == 2) && ((Boolean) value).booleanValue()) {
                if (entity1 != fEntityRep) {
                    UndoableEdit edit = new ActionChangeProperty(fDiagram, fEntity, "Weak", String.valueOf(fEntityRep.isWeak()), String.valueOf(value), entity1, relationship);
                    fEntityRep.addDependency(relationship, false);
                    fDiagram.getEditor().fUndoSupport.postEdit(edit);
                } else {
                    UndoableEdit edit = new ActionChangeProperty(fDiagram, fEntity, "Weak", String.valueOf(fEntityRep.isWeak()), String.valueOf(value), entity2, relationship);
                    fEntityRep.addDependency(relationship, false);
                    fDiagram.getEditor().fUndoSupport.postEdit(edit);
                }
            } else if (col == 2) {
                if (entity1 != fEntityRep) {
                    UndoableEdit edit = new ActionChangeProperty(fDiagram, fEntity, "Weak", String.valueOf(fEntityRep.isWeak()), String.valueOf(value), entity1, relationship);
                    fEntityRep.removeDependency(relationship);
                    fDiagram.getEditor().fUndoSupport.postEdit(edit);
                } else {
                    UndoableEdit edit = new ActionChangeProperty(fDiagram, fEntity, "Weak", String.valueOf(fEntityRep.isWeak()), String.valueOf(value), entity2, relationship);
                    fEntityRep.removeDependency(relationship);
                    fDiagram.getEditor().fUndoSupport.postEdit(edit);
                }
            }
        } catch (Exception e) {
            fDiagram.getEditor().setStatusMessage(e.getMessage());
        }
        fDiagram.repaint();
    }

    public void setEntity(ERDiagram diagram, Entity entity) {
        fEntity = entity;
        fEntityRep = (EntityRep) entity.getRep();
        fDiagram = diagram;
        setPossibleDependentRelationships();
    }

    private void setPossibleDependentRelationships() {
        fPossibleWeakRelationships.clear();
        Iterator itRelationships = fEntityRep.getConnectedRelationships().iterator();
        while (itRelationships.hasNext()) {
            RelationshipRep relationship = (RelationshipRep) itRelationships.next();
            if (fEntityRep.isWeak() || fEntityRep.canBeSetDependentOf(relationship)) {
                fPossibleWeakRelationships.add(relationship);
            }
        }
    }
}

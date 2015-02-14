package UI.Panels;

import Exceptions.RoleException;
import Reps.Role;
import Shapes.Line;
import UI.ActionChangeProperty;
import UI.ERDiagram;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.UndoableEdit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <p>Title: LinePanel</p>
 * <p>Description: A class representing the property panel for lines (with a role)</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 14/06/2003
 */
public class LinePanel extends ElementPanel implements KeyListener {

    private JTextField fTextField = new JTextField();
    private RoleTableModel fTableModel1;
    private RoleRefIntegrityTableModel fTableModel2;
    private JTable fTable1, fTable2;

    public LinePanel() {
        super();
        fTableModel1 = new RoleTableModel();
        fTableModel2 = new RoleRefIntegrityTableModel();
        fTable1 = new JTable(fTableModel1);
        fTable2 = new JTable(fTableModel2);

        fTable1.setRowSelectionAllowed(false);
        fTable2.setRowSelectionAllowed(false);

        add(fTable1);
        add(fTable2);
        fTable1.addKeyListener(this);

        fTable1.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fTextField));
    }

    public void removeFocus() {
        super.removeFocus();
        fTable1.setVisible(false);
    }

    public void keyReleased(KeyEvent ke) {
    }

    public void keyPressed(KeyEvent ke) {
    }

    public void keyTyped(KeyEvent ke) {
        if (!fTextField.hasFocus()) {
            fTextField.grabFocus();
            fTextField.setText("");
        }
    }

    public void update(ERDiagram diagram, Line line) {
        super.update(diagram, line);
        fTable1.setVisible(true);
        fTableModel1.setLine(diagram, line);
        fTableModel1.fireTableStructureChanged();
        fTableModel2.setLine(diagram, line);
        fTableModel2.fireTableStructureChanged();
        fTable1.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fTextField));
    }

}

/**
 * Table Model to display the cardinality of the role
 */
class RoleTableModel extends AbstractTableModel {

    private Role fRole;
    private Line fLine;
    private ERDiagram fDiagram;

    public Object getValueAt(int row, int col) {
        if (row == 0 && col == 0) return "Min Cardinality";
        else if (row == 1 && col == 0) return "Max Cardinality";
        else if (row == 0 && col == 1) return fRole.getMinCard();
        else if (row == 1 && col == 1) return fRole.getMaxCard();
        else return "";
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return 2;
    }

    public boolean isCellEditable(int row, int col) {
        return (col != 0);
    }

    public void setValueAt(Object value, int row, int col) {
        fireTableCellUpdated(row, col);
        fDiagram.setUnedited(false);
        try {
            if (row == 0 && col == 1) {
                UndoableEdit edit = new ActionChangeProperty(fDiagram, fLine, "MinCard", fRole.getMinCard(), (String) value);
                fRole.setMinCard((String) value);
                fDiagram.getEditor().fUndoSupport.postEdit(edit);
            } else if (row == 1 && col == 1) {
                UndoableEdit edit = new ActionChangeProperty(fDiagram, fLine, "MaxCard", fRole.getMaxCard(), (String) value);
                fRole.setMaxCard((String) value);
                fDiagram.getEditor().fUndoSupport.postEdit(edit);
            }
        } catch (RoleException e) {
            fDiagram.getEditor().setStatusMessage(e.getMessage());
        } catch (NumberFormatException e) {
            fDiagram.getEditor().setStatusMessage("Illegal cardinality value");
        } catch (Exception e) {
            fDiagram.getEditor().setStatusMessage(e.getMessage());
        }
        fDiagram.repaint();
    }

    public void setLine(ERDiagram diagram, Line line) {
        fLine = line;
        fRole = (Role) line.getRep();
        fDiagram = diagram;
    }
}

/**
 * Table Model to display the referential integrity property
 */
class RoleRefIntegrityTableModel extends AbstractTableModel {

    private Role fRole;
    private Line fLine;
    private ERDiagram fDiagram;

    public Object getValueAt(int row, int col) {
        if (row == 0 && col == 0) return "Ref. Integrity";
        else if (row == 0 && col == 1) return new Boolean(fRole.getRefIntegrity());
        else return "";
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return 1;
    }

    public boolean isCellEditable(int row, int col) {
        return (col != 0);
    }

    public void setValueAt(Object value, int row, int col) {
        fireTableCellUpdated(row, col);
        fDiagram.setUnedited(false);
        if (row == 0 && col == 1) {
            try {
                UndoableEdit edit = new ActionChangeProperty(fDiagram, fLine, "RefIntegrity", String.valueOf(fRole.getRefIntegrity()), String.valueOf(value));
                fRole.setRefIntegrity(((Boolean) value).booleanValue(), false);
                fDiagram.getEditor().fUndoSupport.postEdit(edit);
            } catch (RoleException e) {
                fDiagram.getEditor().setStatusMessage(e.getMessage());
            }
        }
        fDiagram.repaint();
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void setLine(ERDiagram diagram, Line line) {
        fLine = line;
        fRole = (Role) line.getRep();
        fDiagram = diagram;
    }
}

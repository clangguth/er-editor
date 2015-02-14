package UI.Panels;

import Shapes.DrawableElement;
import Shapes.ISA;
import UI.ActionChangeProperty;
import UI.ERDiagram;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.UndoableEdit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <p>Title: ElementPanel</p>
 * <p>Description: A class representing the property panel for 'named' elements</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 12/06/2003
 */
public class ElementPanel extends JPanel implements KeyListener {

    private JTextField fTextField = new JTextField();
    private DrawableElement fElement;
    private JTable fTable1;
    private ElementTableModel fTableModel1;

    public ElementPanel() {
        fTableModel1 = new ElementTableModel();

        fTable1 = new JTable(fTableModel1);
        fTable1.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fTextField));
        fTable1.setRowSelectionAllowed(false);
        fTable1.addKeyListener(this);

        setLayout(new BoxLayout(this, 3));
        add(fTable1);
    }

    public void removeFocus() {
        fTable1.setVisible(false);
    }

    public void update(ERDiagram diagram, DrawableElement element) {
        fTable1.setVisible(true);
        fElement = element;
        fTableModel1.setElement(diagram, element);
        fTableModel1.fireTableStructureChanged();
        fTable1.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fTextField));
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
}

/**
 * Table Model to display the name of an element (SQL)
 */
class ElementTableModel extends AbstractTableModel {

    private DrawableElement fElement;
    private ERDiagram fDiagram;

    public Object getValueAt(int row, int col) {
        if (row == 0 && col == 0) return "Name";
        else if (row == 0 && col == 1) return fElement.getRep().getName();
        else return "";
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return 1;
    }

    public boolean isCellEditable(int row, int col) {
        return (col != 0 && !(fElement instanceof ISA));
    }

    public void setValueAt(Object value, int row, int col) {
        fireTableCellUpdated(row, col);
        fDiagram.setUnedited(false);
        if (row == 0 && col == 1) {
            try {
                UndoableEdit edit = new ActionChangeProperty(fDiagram, fElement, "Name", fElement.getRep().getName(), (String) value);
                fElement.getRep().setName((String) value);
                fElement.adjustWidthToName(fDiagram);
                fDiagram.getEditor().fUndoSupport.postEdit(edit);
            } catch (Exception e) {
                fDiagram.getEditor().setStatusMessage(e.getMessage());
            }
        }
        fDiagram.repaint();
    }

    public void setElement(ERDiagram diagram, DrawableElement element) {
        fElement = element;
        fDiagram = diagram;
    }
}
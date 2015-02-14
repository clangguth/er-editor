package UI.Panels;

import Exceptions.AttributeRepException;
import Reps.AttributeRep;
import Shapes.Attribute;
import UI.ActionChangeProperty;
import UI.Constants;
import UI.ERDiagram;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.UndoableEdit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <p>Title: AttributePanel</p>
 * <p>Description: A class representing the property panel for attributes</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 14/06/2003
 */
public class AttributePanel extends ElementPanel implements KeyListener {

    private JTextField fTextField2 = new JTextField();
    private JTable fTable2;
    private JTable fTable3;
    private JTable fTable4;
    private JTable fTable5;
    private AttributeTypeTableModel fTableModel2;
    private AttributeOptionsTableModel fTableModel3;
    private AttributeDataTypeTableModel fTableModel4;
    private AttributeLengthTableModel fTableModel5;
    private JComboBox fComboBox1 = new JComboBox(new Object[]{"normal", "key", "multivalued"});
    private JComboBox fComboBox2 = new JComboBox(Constants.kTypeArray);

    public AttributePanel() {
        super();
        fTableModel2 = new AttributeTypeTableModel();
        fTableModel3 = new AttributeOptionsTableModel();
        fTableModel4 = new AttributeDataTypeTableModel();
        fTableModel5 = new AttributeLengthTableModel();
        fTable2 = new JTable(fTableModel2);
        fTable3 = new JTable(fTableModel3);
        fTable4 = new JTable(fTableModel4);
        fTable5 = new JTable(fTableModel5);

        fTable2.setRowSelectionAllowed(false);
        fTable3.setRowSelectionAllowed(false);
        fTable4.setRowSelectionAllowed(false);
        fTable5.setRowSelectionAllowed(false);

        add(fTable2);
        add(fTable3);
        add(fTable4);
        add(fTable5);
        fTable5.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fTextField2));
        fTable2.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fComboBox1));
        fTable4.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fComboBox2));
        fTable5.addKeyListener(this);
    }

    public void keyReleased(KeyEvent ke) {
    }

    public void keyPressed(KeyEvent ke) {
    }

    public void keyTyped(KeyEvent ke) {
        if (fTable5.hasFocus()) {
            if (!fTextField2.hasFocus()) {
                fTextField2.grabFocus();
                fTextField2.setText("");
            }
        } else {
            super.keyTyped(ke);
        }
    }

    public void removeFocus() {
        super.removeFocus();
        fTable2.setVisible(false);
        fTable3.setVisible(false);
        fTable4.setVisible(false);
        fTable5.setVisible(false);
    }

    public void update(ERDiagram diagram, Attribute attribute) {
        super.update(diagram, attribute);

        fTable2.setVisible(true);
        fTable3.setVisible(true);
        fTable4.setVisible(true);
        fTable5.setVisible(true);

        fTableModel2.fireTableStructureChanged();
        fTableModel3.fireTableStructureChanged();
        fTableModel4.fireTableStructureChanged();
        fTableModel5.fireTableStructureChanged();

        fTableModel2.setAttribute(diagram, attribute);
        fTableModel3.setAttribute(diagram, attribute);
        fTableModel4.setAttribute(diagram, this, attribute);
        fTableModel5.setAttribute(diagram, attribute);

        fTable5.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fTextField2));
        fTable2.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fComboBox1));
        fTable4.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(fComboBox2));
    }
}

/**
 * Table Model to display the type of an attribute (key, multivalued, normal)
 */
class AttributeTypeTableModel extends AbstractTableModel {

    private AttributeRep fAttributeRep;
    private Attribute fAttribute;
    private ERDiagram fDiagram;

    public Object getValueAt(int row, int col) {
        if (row == 0 && col == 0) return "Type";
        else if (row == 0 && col == 1) return fAttributeRep.getType();
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
                UndoableEdit edit = new ActionChangeProperty(fDiagram, fAttribute, "Type", fAttributeRep.getType(), (String) value);
                fAttributeRep.setType(value.toString());
                fDiagram.getEditor().fUndoSupport.postEdit(edit);
            } catch (AttributeRepException e) {
                fDiagram.getEditor().setStatusMessage(e.getMessage());
            }
        }
        fDiagram.repaint();
    }

    public void setAttribute(ERDiagram diagram, Attribute attribute) {
        fAttribute = attribute;
        fAttributeRep = (AttributeRep) attribute.getRep();
        fDiagram = diagram;
    }
}

/**
 * Table Model to display the 'required' property of an attribute
 */
class AttributeOptionsTableModel extends AbstractTableModel {

    private AttributeRep fAttributeRep;
    private Attribute fAttribute;
    private ERDiagram fDiagram;

    public Object getValueAt(int row, int col) {
        if (row == 0 && col == 0) return "Required";
        else if (row == 1 && col == 0) return "Unique";
        else if (row == 0 && col == 1) return new Boolean(fAttributeRep.isRequired());
        else if (row == 1 && col == 1) return new Boolean(fAttributeRep.isUnique());
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

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void setValueAt(Object value, int row, int col) {
        fireTableCellUpdated(row, col);
        fDiagram.setUnedited(false);
        try {
            if (row == 0 && col == 1) {
                UndoableEdit edit = new ActionChangeProperty(fDiagram, fAttribute, "Required", String.valueOf(fAttributeRep.isRequired()), String.valueOf(value));
                fAttributeRep.setRequired(((Boolean) value).booleanValue());
                fDiagram.getEditor().fUndoSupport.postEdit(edit);
            } else if (row == 1 && col == 1) {
                UndoableEdit edit = new ActionChangeProperty(fDiagram, fAttribute, "Unique", String.valueOf(fAttributeRep.isUnique()), String.valueOf(value));
                fAttributeRep.setUnique(((Boolean) value).booleanValue());
                fDiagram.getEditor().fUndoSupport.postEdit(edit);
            }
        } catch (AttributeRepException e) {
            fDiagram.getEditor().setStatusMessage(e.getMessage());
        }
    }

    public void setAttribute(ERDiagram diagram, Attribute attribute) {
        fAttribute = attribute;
        fAttributeRep = (AttributeRep) attribute.getRep();
        fDiagram = diagram;
    }
}

/**
 * Table Model to display the datatype of an attribute (serial, char, varchar, ...)
 */
class AttributeDataTypeTableModel extends AbstractTableModel {

    private AttributeRep fAttributeRep;
    private Attribute fAttribute;
    private ERDiagram fDiagram;
    private AttributePanel fPanel;

    public Object getValueAt(int row, int col) {
        if (row == 0 && col == 0) return "Datatype";
        else if (row == 0 && col == 1) return fAttributeRep.getDataType();
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
        if (row == 0 && col == 1) {
            UndoableEdit edit = new ActionChangeProperty(fDiagram, fAttribute, "DataType", fAttributeRep.getDataType(), (String) value);
            fAttributeRep.setDataType((String) value);
            fDiagram.getEditor().fUndoSupport.postEdit(edit);
            fPanel.revalidate();
            fPanel.repaint();
        }
    }

    public void setAttribute(ERDiagram diagram, AttributePanel panel, Attribute attribute) {
        fAttribute = attribute;
        fAttributeRep = (AttributeRep) attribute.getRep();
        fDiagram = diagram;
        fPanel = panel;
    }
}

/**
 * Table Model to display to length of an attribute (SQL)
 */
class AttributeLengthTableModel extends AbstractTableModel {

    private AttributeRep fAttributeRep;
    private Attribute fAttribute;
    private ERDiagram fDiagram;

    public Object getValueAt(int row, int col) {
        if (row == 0 && col == 0) return "Length";
        else if (row == 0 && col == 1) return new Integer(fAttributeRep.getLength());
        else return "";
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        boolean showLength = false;
        if (fAttributeRep != null) {
            for (int i = 0; i < Constants.kTypeWithLength.length; i++) {
                if (Constants.kTypeWithLength[i].equals(fAttributeRep.getDataType())) showLength = true;
            }
            if (showLength) return 1;
        }
        return 0;
    }

    public boolean isCellEditable(int row, int col) {
        return (col != 0);
    }

    public void setValueAt(Object value, int row, int col) {
        fireTableCellUpdated(row, col);
        try {
            if (row == 0 && col == 1) {
                UndoableEdit edit = new ActionChangeProperty(fDiagram, fAttribute, "Length", String.valueOf(fAttributeRep.getLength()), (String) value);
                fAttributeRep.setLength(Integer.parseInt((String) value));
                fDiagram.getEditor().fUndoSupport.postEdit(edit);
            }
            fDiagram.setUnedited(false);
        } catch (NumberFormatException e) {
            fDiagram.getEditor().setStatusMessage("Illegal numerical value");
        }
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public void setAttribute(ERDiagram diagram, Attribute attribute) {
        fAttribute = attribute;
        fAttributeRep = (AttributeRep) attribute.getRep();
        fDiagram = diagram;
    }
}

package UI.Panels;

import Mapping.Relation;
import Reps.AttributeRep;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: MapPanel</p>
 * <p>Description: A Panel with the Relational Mapping for the active diagram</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 22/06/2003
 */
public class MapPanel extends JDialog {

    /* Different styles */
    static final String NORMAL = "Normal";
    static final String UNDERLINED = "Underlined"; /* For key attributes */
    static final String BOLD = "Bold"; /* For the relation name */
    private Style fStyleNormal;
    private Style fStyleUnderlined;
    private Style fStyleBold;

    /* GUI components */
    private DefaultStyledDocument fDoc;
    private JTextPane fPane;

    public MapPanel() {
    /* Initialise GUI */
        setModal(true);
        fDoc = new DefaultStyledDocument();
        fPane = new JTextPane(fDoc);
        fPane.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(fPane);
        getContentPane().add(scrollPane);

        setSize(600, 300);
        setTitle("ER Mapping");

        Dimension dim = getToolkit().getScreenSize();
        Rectangle abounds = getBounds();
        setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);

        fStyleNormal = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        fStyleUnderlined = fPane.addStyle("underlined", fStyleNormal);
        fStyleBold = fPane.addStyle("bold", fStyleNormal);

        StyleConstants.setUnderline(fStyleUnderlined, true);
        StyleConstants.setFontSize(fStyleNormal, 16);
        StyleConstants.setBold(fStyleBold, true);
    }

    /**
     * Print the relations
     *
     * @param relations Relations to print in this panel
     */
    public void print(final ArrayList relations) {
        Iterator itRelations = relations.iterator();
        while (itRelations.hasNext()) {
            Relation relation = (Relation) itRelations.next();
            printRelation(relation);
        }
    }

    /**
     * Print one relation
     *
     * @param relation Relation to print
     */
    private void printRelation(final Relation relation) {
        try {
      /* Name of the relation (bold) */
            String str = relation.getName();
            fDoc.insertString(fDoc.getLength(), str, fStyleBold);
            fDoc.insertString(fDoc.getLength(), "(", fStyleNormal);

      /* Key Attributes (underlined */
            Iterator itAttributes = relation.getKeyAttributes().iterator();
            while (itAttributes.hasNext()) {
                str = "";
                AttributeRep attribute = (AttributeRep) itAttributes.next();
                if (!attribute.getType().equals(AttributeRep.kMultivalued)) {
                    if (relation.alreadyExists(attribute) || attribute.getAttributedElement().getName() != relation.getName()) {
                        str = attribute.getAttributedElement().getName() + ".";
                    }
                }
                str += attribute.getName();
                fDoc.insertString(fDoc.getLength(), str, fStyleUnderlined);
                if (itAttributes.hasNext()) {
                    fDoc.insertString(fDoc.getLength(), ", ", fStyleNormal);
                }
            }

      /* Non-key attributes */
            ArrayList nonKeyAttributes = relation.getNonKeyAttributes();
            if (!nonKeyAttributes.isEmpty()) {
                fDoc.insertString(fDoc.getLength(), ", ", fStyleNormal);
                itAttributes = nonKeyAttributes.iterator();
                while (itAttributes.hasNext()) {
                    str = "";
                    AttributeRep attribute = (AttributeRep) itAttributes.next();
                    if (relation.alreadyExists(attribute) || attribute.getAttributedElement().getName() != relation.getName()) {
                        str = attribute.getAttributedElement().getName() + ".";
                    }
                    str += attribute.getName();
                    fDoc.insertString(fDoc.getLength(), str, fStyleNormal);
                    if (itAttributes.hasNext()) {
                        fDoc.insertString(fDoc.getLength(), ", ", fStyleNormal);
                    }
                }
            }

            fDoc.insertString(fDoc.getLength(), ")\n", fStyleNormal);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error While Mapping", JOptionPane.ERROR_MESSAGE);
        }
    }

}
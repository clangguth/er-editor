package Shapes;

import Exceptions.LineException;
import Reps.AttributeRep;
import Reps.AttributedElement;
import Reps.Element;
import Reps.Role;
import UI.Constants;
import UI.ERDiagram;
import UI.EREditor;
import UI.Panels.AttributePanel;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * <p>Title: Attribute</p>
 * <p>Description: A class representing a visible attribute (ellipse)</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 20/06/2003
 */
public class Attribute extends DrawableElement {

    /* Propery panel for attributes */
    private static AttributePanel fPanel = new AttributePanel();

    /* Internal attribute */
    private AttributeRep fAttributeRep;

    /* Visible attribute (ellipse) */
    private Ellipse2D fEllipse;

    /**
     * Constructor
     *
     * @param diagram      Diagram
     * @param attributeRep Internal attribute
     * @param x            X-coördinate on the diagram
     * @param y            Y-coordinate on the diagram
     */
    public Attribute(final ERDiagram diagram, final AttributeRep attributeRep, final int x, final int y) {
        super(diagram, Constants.kAttributeWidth, Constants.kAttributeHeight);
        fAttributeRep = attributeRep;
        fEllipse = new Ellipse2D.Double(x - (Constants.kAttributeWidth / 2),
                y - (Constants.kAttributeHeight / 2), Constants.kAttributeWidth, Constants.kAttributeHeight);
    }

    /**
     * Returns the 'fill color' for an attribute (based on the colors in Constants.java)
     *
     * @return fill color
     */
    protected Color getColor() {
        return Constants.kAttributeColor;
    }

    /**
     * Returns the visible shape, belonging to an attribute (Ellipse)
     *
     * @return visible shape
     */
    protected Shape getShape() {
        return fEllipse;
    }

    /**
     * Returns the internal element, belonging to an attribute (AttributeRep)
     *
     * @return internal element
     */
    public Element getRep() {
        return fAttributeRep;
    }

    /**
     * Shows the property panel for an attribute
     *
     * @param diagram Diagram
     */
    public void showPanel(final ERDiagram diagram) {
        EREditor editor = diagram.getEditor();
        fPanel.update(diagram, this);
        editor.showPanel(fPanel);
    }

    /**
     * Update the position field of the internal element (done before writing to a file)
     */
    public void updateRepPosition() {
        int difX = (int) ((getWidth() - Constants.kAttributeWidth) / 2);
        getRep().setPosition(getCenterX() - difX, getCenterY());
    }

    /**
     * Connect this attribute with another element
     *
     * @param role            Role of the connection line (or null if it should be automatically created)
     * @param drawableElement Element to connect with
     * @return Role of the connection
     * @throws LineException These elements cannot be connected
     */
    public Role connect(final Role role, final DrawableElement drawableElement) throws LineException {
        try {
            if (drawableElement instanceof Entity || drawableElement instanceof Relationship) {
        /* Connect this attribute with an entity or a relationship */
                ((AttributedElement) drawableElement.getRep()).addAttribute(fAttributeRep);
            } else {
        /* Every other connection element than an entity or a relationship is invalid */
                throw new LineException("These elements cannot be connected...");
            }
            return null;
        } catch (Exception e) {
            throw new LineException(e.getMessage());
        }
    }

    /**
     * Returns if this attribute can be connected with a given object
     *
     * @param drawableElement Object to connect with
     * @return boolean to indicate if connection is allowed
     */
    public boolean canBeConnectedTo(final DrawableElement drawableElement) {
        if (drawableElement instanceof Entity || drawableElement instanceof Relationship) {
            return ((AttributedElement) drawableElement.getRep()).canAddAttribute(fAttributeRep);
        } else {
            return false;
        }
    }

    /**
     * Disconnect this attribute with the given element
     *
     * @param role            Role belonging to the connection
     * @param drawableElement Element to disconnect with
     * @throws LineException No connection found between these elements
     */
    public void disconnect(final Role role, final DrawableElement drawableElement) throws LineException {
        try {
            if (drawableElement instanceof Entity || drawableElement instanceof Relationship) {
                ((AttributedElement) drawableElement.getRep()).removeAttribute(fAttributeRep);
            } else {
                throw new LineException("No connection found between these elements...");
            }
        } catch (Exception e) {
            throw new LineException(e.getMessage());
        }
    }

    /**
     * Move this attribute to a given location (used for dragging)
     *
     * @param newX New X-coordinate
     * @param newY New Y-coördinate
     */
    protected void updateLocation(final int newX, final int newY) {
        fEllipse.setFrame(newX - (getWidth() / 2), newY - (Constants.kAttributeHeight / 2), fEllipse.getWidth(), fEllipse.getHeight());
    }

    /**
     * Returns the handles for this attribute
     *
     * @return handles for this attribute
     */
    protected ArrayList getHandles() {
        ArrayList handles = new ArrayList();
        int width = (int) getWidth();
        handles.add(new Handle(0, -(Constants.kAttributeHeight / 2)));
        handles.add(new Handle(0, Constants.kAttributeHeight / 2));
        handles.add(new Handle(-(width / 2), 0));
        handles.add(new Handle(width / 2, 0));
        return handles;
    }

    /**
     * Adjust the width of this attribute to the name length
     *
     * @param diagram Diagram
     */
    public void adjustWidthToName(final ERDiagram diagram) {
        double length = getNameStringLength((Graphics2D) diagram.getGraphics());
        if (length > (Constants.kAttributeWidth - 20)) {
      /* adjusted attribute width */
            fEllipse.setFrame(fEllipse.getX(), fEllipse.getY(), length + 20, Constants.kAttributeHeight);
            setWidth((int) length + 20);
        } else {
      /* normal attribute width */
            fEllipse.setFrame(fEllipse.getX(), fEllipse.getY(), Constants.kAttributeWidth, Constants.kAttributeHeight);
            setWidth(Constants.kAttributeWidth);
        }
    }

    /**
     * Draws this attribute on the diagram
     *
     * @param g2    Place to draw on
     * @param color Drawing color (normal or selection color)
     */
    public void draw(final Graphics2D g2, final Color color) {
        super.draw(g2, color);
        if (fAttributeRep.getType().equals(AttributeRep.kMultivalued)) {
       /* draw a second ellipse because the attribute is multivalued */
            g2.drawOval((int) getCenterX() - ((int) getWidth() / 2) + 3, (int) getCenterY() - (Constants.kAttributeHeight / 2) + 3, (int) getWidth() - 6, (int) getHeight() - 6);
        } else if (fAttributeRep.getType().equals(AttributeRep.kKey)) {
       /* draw the name underlined, because the attribute is a key attribute */
            int nameLength = (int) getNameStringLength(g2);
            g2.drawLine((int) getCenterX() - (nameLength / 2), (int) getCenterY() + 3, (int) getCenterX() - (nameLength / 2) + (int) getNameStringLength(g2), (int) getCenterY() + 3);
        }
    }

}

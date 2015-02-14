package Shapes;

import Exceptions.LineException;
import Reps.*;
import UI.Constants;
import UI.ERDiagram;
import UI.EREditor;
import UI.Panels.ElementPanel;

import java.awt.*;
import java.util.ArrayList;

/**
 * <p>Title: Relationship</p>
 * <p>Description: A class representing a visible relationship (diamond)</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 20/06/2003
 */
public class Relationship extends DrawableElement {

    /* Propery panel for relationships */
    private static ElementPanel fPanel = new ElementPanel();

    /* Internal relationship */
    private RelationshipRep fRelationshipRep;

    /* Visible relationship (diamond) */
    private Polygon fDiamond;

    /**
     * Constructor
     *
     * @param diagram         Diagram
     * @param relationshipRep Internal relationship
     * @param x               X-coördinate on the diagram
     * @param y               Y-coordinate on the diagram
     */
    public Relationship(final ERDiagram diagram, final RelationshipRep relationshipRep, final int x, final int y) {
        super(diagram, Constants.kRelationshipWidth, Constants.kRelationshipHeight);
        fDiamond = new Polygon();
        int xPoints[] = {x - (Constants.kRelationshipWidth / 2), x, x + (Constants.kRelationshipWidth / 2), x};
        int yPoints[] = {y, y - (Constants.kRelationshipHeight / 2), y, y + (Constants.kRelationshipHeight / 2)};
        fDiamond.xpoints = xPoints;
        fDiamond.ypoints = yPoints;
        fDiamond.npoints = 4;
        fRelationshipRep = relationshipRep;
    }

    /**
     * Returns the visible shape, belonging to a relationship (Diamond)
     *
     * @return visible shape
     */
    protected Shape getShape() {
        return fDiamond;
    }

    /**
     * Update the position field of the internal element (done before writing to a file)
     */
    public void updateRepPosition() {
        int difX = (int) ((getWidth() - Constants.kRelationshipWidth) / 2);
        getRep().setPosition(getCenterX() - difX, getCenterY());
    }

    /**
     * Returns the 'fill color' for a relationship (based on the colors in Constants.java)
     *
     * @return fill color
     */
    protected Color getColor() {
        return Constants.kRelationshipColor;
    }

    /**
     * Returns the internal element, belonging to a relationship (RelationshipRep)
     *
     * @return internal element
     */
    public Element getRep() {
        return fRelationshipRep;
    }

    /**
     * Shows the property panel for a relationship
     *
     * @param diagram Diagram
     */
    public void showPanel(final ERDiagram diagram) {
        EREditor editor = diagram.getEditor();
        fPanel.update(diagram, this);
        editor.showPanel(fPanel);
    }

    /**
     * Connect this relationship with another element
     *
     * @param role            Role of the connection line (or null if it should be automatically created)
     * @param drawableElement Element to connect with
     * @return Role of the connection
     * @throws LineException These elements cannot be connected
     */
    public Role connect(Role role, final DrawableElement drawableElement) throws LineException {
        try {
            if (drawableElement instanceof Attribute) {
        /* Connect this relationship with an attribute */
                fRelationshipRep.addAttribute((AttributeRep) drawableElement.getRep());
            } else if (drawableElement instanceof Entity) {
        /* Connect this relationship with an entity */
                if (role == null) role = new Role((EntityRep) drawableElement.getRep(), fRelationshipRep);
                fRelationshipRep.addRole(role);
            } else {
        /* Every other connection element is invalid */
                throw new LineException("These elements cannot be connected...");
            }
            return role;
        } catch (Exception e) {
            throw new LineException(e.getMessage());
        }
    }

    /**
     * Returns if this relationship can be connected with a given object
     *
     * @param drawableElement Object to connect with
     * @return boolean to indicate if connection is allowed
     */
    public boolean canBeConnectedTo(final DrawableElement drawableElement) {
        if (drawableElement instanceof Attribute) {
            return fRelationshipRep.canAddAttribute((AttributeRep) drawableElement.getRep());
        } else if (drawableElement instanceof Entity) {
            Role role = new Role((EntityRep) drawableElement.getRep(), fRelationshipRep);
            return fRelationshipRep.canAddRole(role);
        } else {
            return false;
        }
    }

    /**
     * Disconnect this relationship with the given element
     *
     * @param role            Role belonging to the connection
     * @param drawableElement Element to disconnect with
     * @throws LineException No connection found between these elements
     */
    public void disconnect(final Role role, final DrawableElement drawableElement) throws LineException {
        try {
            if (drawableElement instanceof Attribute) {
                fRelationshipRep.removeAttribute((AttributeRep) drawableElement.getRep());
            } else if (drawableElement instanceof Entity) {
                fRelationshipRep.removeRole(role);
            } else {
                throw new LineException("No connection found between these elements...");
            }
        } catch (Exception e) {
            throw new LineException(e.getMessage());
        }
    }

    /**
     * Move this relationship to a given location (used for dragging)
     *
     * @param newX New X-coordinate
     * @param newY New Y-coördinate
     */
    protected void updateLocation(final int newX, final int newY) {
        int xPoints[] = {newX - ((int) getWidth() / 2), newX, newX + ((int) getWidth() / 2), newX};
        int yPoints[] = {newY, newY - (Constants.kRelationshipHeight / 2), newY, newY + (Constants.kRelationshipHeight / 2)};
        fDiamond.xpoints = xPoints;
        fDiamond.ypoints = yPoints;
        fDiamond.npoints = 4;
        fDiamond.invalidate();
    }

    /**
     * Returns the handles for this relationship
     *
     * @return handles for this relationship
     */
    protected ArrayList getHandles() {
        ArrayList handles = new ArrayList();
        int width = (int) getWidth();
        handles.add(new Handle(-(width / 2), 0));
        handles.add(new Handle(0, -(Constants.kRelationshipHeight / 2)));
        handles.add(new Handle(width / 2, 0));
        handles.add(new Handle(0, (Constants.kRelationshipHeight / 2)));
        return handles;
    }

    /**
     * Adjust the width of this relationship to the name length
     *
     * @param diagram Diagram
     */
    public void adjustWidthToName(final ERDiagram diagram) {
        double length = getNameStringLength((Graphics2D) diagram.getGraphics());
        if (length > (Constants.kRelationshipWidth - 20)) {
      /* adjusted relationship width */
            int xPoints[] = {fDiamond.xpoints[0], fDiamond.xpoints[0] + ((int) length / 2) + 20, fDiamond.xpoints[0] + (int) length + 40, fDiamond.xpoints[0] + ((int) length / 2) + 20};
            fDiamond.xpoints = xPoints;
            fDiamond.invalidate();
            setWidth((int) length + 40);
        } else {
      /* normal relationship width */
            int xPoints[] = {fDiamond.xpoints[0], fDiamond.xpoints[0] + (Constants.kRelationshipWidth / 2), fDiamond.xpoints[0] + Constants.kRelationshipWidth, fDiamond.xpoints[0] + (Constants.kRelationshipWidth / 2)};
            fDiamond.xpoints = xPoints;
            fDiamond.invalidate();
            setWidth(Constants.kRelationshipWidth);
        }
    }

    /**
     * Draws this relationship on the diagram
     *
     * @param g2    Place to draw on
     * @param color Drawing color (normal or selection color)
     */
    public void draw(final Graphics2D g2, final Color color) {
        super.draw(g2, color);
        if (fRelationshipRep.isWeak()) {
      /* draw a second diamond because the entity is weak */
            int xPoints[] = {fDiamond.xpoints[0] + 6, fDiamond.xpoints[1], fDiamond.xpoints[2] - 6, fDiamond.xpoints[3]};
            int yPoints[] = {fDiamond.ypoints[0], fDiamond.ypoints[1] + 5, fDiamond.ypoints[2], fDiamond.ypoints[3] - 5};
            g2.draw(new Polygon(xPoints, yPoints, 4));
        }
    }
}

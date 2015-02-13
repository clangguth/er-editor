package Shapes;

import Exceptions.LineException;
import Reps.*;
import UI.Constants;
import UI.ERDiagram;
import UI.EREditor;
import UI.Panels.EntityPanel;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * <p>Title: Entity</p>
 * <p>Description: A class representing a visible entity (rectangle)</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 20/06/2003
 */
public class Entity extends DrawableElement {

    /* Propery panel for entities */
    private static EntityPanel fPanel = new EntityPanel();

    /* Internal entity */
    private EntityRep fEntityRep;

    /* Visible entity (rectangle) */
    private Rectangle2D fRectangle;

    /**
     * Constructor
     *
     * @param diagram   Diagram
     * @param entityRep Internal entity
     * @param x         X-coördinate on the diagram
     * @param y         Y-coordinate on the diagram
     */
    public Entity(final ERDiagram diagram, final EntityRep entityRep, final int x, final int y) {
        super(diagram, Constants.kEntityWidth, Constants.kEntityHeight);
        fRectangle = new Rectangle2D.Double(x - (Constants.kEntityWidth / 2), y - (Constants.kEntityHeight / 2), Constants.kEntityWidth, Constants.kEntityHeight);
        fEntityRep = entityRep;
    }

    /**
     * Returns the visible shape, belonging to an entity (Rectangle)
     *
     * @return visible shape
     */
    protected Shape getShape() {
        return fRectangle;
    }

    /**
     * Update the position field of the internal element (done before writing to a file)
     */
    public void updateRepPosition() {
        int difX = (int) ((getWidth() - Constants.kEntityWidth) / 2);
        getRep().setPosition(getCenterX() - difX, getCenterY());
    }

    /**
     * Returns the 'fill color' for an entity (based on the colors in Constants.java)
     *
     * @return fill color
     */
    protected Color getColor() {
        return Constants.kEntityColor;
    }

    /**
     * Returns the internal element, belonging to an entity (EntityRep)
     *
     * @return internal element
     */
    public Element getRep() {
        return fEntityRep;
    }

    /**
     * Shows the property panel for an entity
     *
     * @param diagram Diagram
     */
    public void showPanel(final ERDiagram diagram) {
        EREditor editor = diagram.getEditor();
        fPanel.update(diagram, this);
        editor.showPanel(fPanel);
    }

    /**
     * Connect this entity with another element
     *
     * @param role            Role of the connection line (or null if it should be automatically created)
     * @param drawableElement Element to connect with
     * @return Role of the connection
     * @throws LineException These elements cannot be connected
     */
    public Role connect(Role role, final DrawableElement drawableElement) throws LineException {
        try {
            if (drawableElement instanceof Attribute) {
        /* Connect this entity with an attribute */
                fEntityRep.addAttribute((AttributeRep) drawableElement.getRep());
            } else if (drawableElement instanceof Relationship) {
        /* Connect this entity with a relationship */
                if (role == null) role = new Role(fEntityRep, (RelationshipRep) drawableElement.getRep());
                ((RelationshipRep) drawableElement.getRep()).addRole(role);
            } else if (drawableElement instanceof ISA) {
        /* Connect this entity with a ISA relationship => entity becomes super entity */
                ((ISARep) drawableElement.getRep()).setSuperEntity(fEntityRep);
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
     * Returns if this entity can be connected with a given object
     *
     * @param drawableElement Object to connect with
     * @return boolean to indicate if connection is allowed
     */
    public boolean canBeConnectedTo(final DrawableElement drawableElement) {
        Role role = null;
        if (drawableElement instanceof Attribute) {
            return fEntityRep.canAddAttribute((AttributeRep) drawableElement.getRep());
        } else if (drawableElement instanceof Relationship) {
            role = new Role(fEntityRep, (RelationshipRep) drawableElement.getRep());
            return ((RelationshipRep) drawableElement.getRep()).canAddRole(role);
        } else if (drawableElement instanceof ISA) {
            return ((ISARep) drawableElement.getRep()).canSetSuperEntity(fEntityRep);
        } else {
            return false;
        }
    }

    /**
     * Disconnect this entity with the given element
     *
     * @param role            Role belonging to the connection
     * @param drawableElement Element to disconnect with
     * @throws LineException No connection found between these elements
     */
    public void disconnect(final Role role, final DrawableElement drawableElement) throws LineException {
        try {
            if (drawableElement instanceof Attribute) {
                fEntityRep.removeAttribute((AttributeRep) drawableElement.getRep());
            } else if (drawableElement instanceof Relationship) {
                ((RelationshipRep) drawableElement.getRep()).removeRole(role);
            } else if (drawableElement instanceof ISA) {
                ((ISARep) drawableElement.getRep()).removeEntity(fEntityRep);
            } else {
                throw new LineException("No connection found between these elements...");
            }
        } catch (Exception e) {
            throw new LineException(e.getMessage());
        }
    }

    /**
     * Move this entity to a given location (used for dragging)
     *
     * @param newX New X-coordinate
     * @param newY New Y-coördinate
     */
    protected void updateLocation(final int newX, final int newY) {
        fRectangle.setFrame(newX - (getWidth() / 2), newY - (Constants.kEntityHeight / 2), fRectangle.getWidth(), fRectangle.getHeight());
    }

    /**
     * Returns the handles for this entity
     *
     * @return handles for this entity
     */
    protected ArrayList getHandles() {
        ArrayList handles = new ArrayList();
        int width = (int) getWidth();
        handles.add(new Handle(-(width / 2), -(Constants.kEntityHeight / 2)));
        handles.add(new Handle(width / 2, -(Constants.kEntityHeight / 2)));
        handles.add(new Handle(width / 2, Constants.kEntityHeight / 2));
        handles.add(new Handle(-(width / 2), Constants.kEntityHeight / 2));
        return handles;
    }

    /**
     * Adjust the width of this entity to the name length
     *
     * @param diagram Diagram
     */
    public void adjustWidthToName(final ERDiagram diagram) {
        double length = getNameStringLength((Graphics2D) diagram.getGraphics());
        if (length > (Constants.kEntityWidth - 20)) {
      /* adjusted entity width */
            fRectangle.setFrame(fRectangle.getX(), fRectangle.getY(), length + 20, Constants.kEntityHeight);
            setWidth((int) length + 20);
        } else {
      /* normal entity width */
            fRectangle.setFrame(fRectangle.getX(), fRectangle.getY(), Constants.kEntityWidth, Constants.kEntityHeight);
            setWidth(Constants.kEntityWidth);
        }
    }

    /**
     * Draws this entity on the diagram
     *
     * @param g2    Place to draw on
     * @param color Drawing color (normal or selection color)
     */
    public void draw(final Graphics2D g2, final Color color) {
        super.draw(g2, color);
        if (fEntityRep.isWeak()) {
      /* draw a second rectangle because the entity is weak */
            g2.draw(new Rectangle2D.Double(getCenterX() - (getWidth() / 2) + 3, getCenterY() - (getHeight() / 2) + 3, getWidth() - 6, getHeight() - 6));
        }
    }

}
package Shapes;

import Exceptions.LineException;
import Reps.Element;
import Reps.Role;
import UI.ERDiagram;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: DrawableElement</p>
 * <p>Description: A class representing a visible element</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 20/06/2003
 */
public abstract class DrawableElement {

    /* List with handles for the selection */
    private ArrayList fHandles = new ArrayList();

    /* Height and with of this element */
    private int fHeight, fWidth;

    /* Diagram to which this element belongs */
    private ERDiagram fDiagram;

    /**
     * Constructor
     *
     * @param diagram Diagram to which this element belongs
     * @param width   Width
     * @param height  Height
     */
    public DrawableElement(final ERDiagram diagram, final int width, final int height) {
        fWidth = width;
        fHeight = height;
        fHandles = getHandles();
        fDiagram = diagram;
    }

    /**
     * Returns the width of the element
     *
     * @return width of this element
     */
    public double getWidth() {
        return fWidth;
    }

    /**
     * Returns the height of the element
     *
     * @return height of this element
     */
    public double getHeight() {
        return fHeight;
    }

    /**
     * Update the width of this element
     *
     * @param width New width
     */
    protected void setWidth(final int width) {
        fWidth = width;
    /* Update the handles */
        fHandles = getHandles();
    }

    /**
     * Returns if a given point lies inside this element
     *
     * @param testX X-coördinate
     * @param testY Y-coördinate
     * @return boolean to indicate if the point lies within the element
     */
    public boolean inside(final int testX, final int testY) {
        return getShape().contains(testX, testY);
    }

    /**
     * Translate this element to a new location
     *
     * @param x Translation width
     * @param y Translation height
     */
    public void translate(final int x, final int y) {
        updateLocation(getCenterX() + x, getCenterY() + y);
    }

    /**
     * Returns the length nessecary to draw the name on the diagram
     *
     * @param g2 Place to draw on
     * @return nessecary draw length of the string
     */
    protected double getNameStringLength(final Graphics2D g2) {
        String name = getRep().getName();
        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D bounds = g2.getFont().getStringBounds(name, frc);
        return bounds.getWidth();
    }

    /**
     * Returns the center X-coördinate of this element
     *
     * @return center-x
     */
    public int getCenterX() {
        return (int) getShape().getBounds().getCenterX();
    }

    /**
     * Returns the center Y-coordinate of this element
     *
     * @return center-y
     */
    public int getCenterY() {
        return (int) getShape().getBounds().getCenterY();
    }

    /**
     * Returns the most left bound of this element
     *
     * @return most left bound
     */
    public int getLeft() {
        return getCenterX() - (int) (fWidth / 2);
    }

    /**
     * Returns the most right bound of this element
     *
     * @return most right bound
     */
    public int getRight() {
        return getCenterX() + (int) (fWidth / 2);
    }

    /**
     * Returns the top bound of this element
     *
     * @return top bound
     */
    public int getTop() {
        return getCenterY() - (int) (fHeight / 2);
    }

    /**
     * Returns the bottom bound of this element
     *
     * @return bottom bound
     */
    public int getBottom() {
        return getCenterY() + (int) (fHeight / 2);
    }

    /**
     * Draws this element on the diagram
     *
     * @param g2    Place to draw on
     * @param color Drawing color (normal or selection color)
     */
    public void draw(final Graphics2D g2, final Color color) {
        if (color != Color.lightGray && fDiagram.getEditor().isColoredDrawing()) {
      /* Fill element with a color */
            g2.setColor(getColor());
        } else {
      /* Clear element (to remove lines within this element
         because the lines go to the middle of the element */
            g2.setColor(Color.WHITE);
        }
        g2.fill(getShape());
        g2.setColor(color);
        g2.draw(getShape());
        drawName(g2);
    }

    /**
     * Draw the selection handlers of this element on the diagram
     *
     * @param g2 Place to draw on
     */
    public void drawHandles(final Graphics2D g2) {
        Iterator itHandles = fHandles.iterator();
        while (itHandles.hasNext()) {
            Handle handle = (Handle) itHandles.next();
            handle.draw(getCenterX(), getCenterY(), g2);
        }
    }

    /**
     * Returns the list with all the lines connected to this element
     *
     * @return list with elements connected to this one
     */
    public ArrayList getConnectedLinesWith() {
        ArrayList lines = new ArrayList();
        Iterator itDrawableElements = fDiagram.getElements().iterator();
        while (itDrawableElements.hasNext()) {
            DrawableElement currentElement = (DrawableElement) itDrawableElements.next();
            if (currentElement instanceof Line) {
                Line line = (Line) currentElement;
                DrawableElement connectedElement = line.isConnectedTo(this);
                if (connectedElement != null) lines.add(line);
            }
        }
        return lines;
    }

    /**
     * Draws the name of this element on the diagram
     *
     * @param g2 Place to draw on
     */
    private void drawName(final Graphics2D g2) {
        String name = getRep().getName();
        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D bounds = g2.getFont().getStringBounds(name, frc);
        float width = (float) bounds.getWidth();
        g2.drawString(name, (int) getCenterX() - width / 2, (int) getCenterY() + 2);
    }

    /**
     * Returns the internal element, belonging to this drawable element
     *
     * @return internal element
     */
    abstract public Element getRep();

    /**
     * Returns the visible shape, belonging to this drawable element (Rectangle, Ellipse, ...)
     *
     * @return visible shape
     */
    abstract protected Shape getShape();

    /**
     * Returns the 'fill color' for this element (if color mode is set 'on')
     *
     * @return fill color
     */
    abstract protected Color getColor();

    /**
     * Shows the property panel belonging to this element
     *
     * @param diagram Diagram
     */
    abstract public void showPanel(final ERDiagram diagram);

    /**
     * Update the position field of the internal element (done before writing to a file)
     */
    abstract public void updateRepPosition();

    /**
     * Adjust the width of this element to the name length
     *
     * @param diagram Diagram
     */
    abstract public void adjustWidthToName(final ERDiagram diagram);

    /**
     * Returns the handles appropriate for this element
     *
     * @return handles for this element
     */
    abstract protected ArrayList getHandles();

    /**
     * Connect this element with another one
     *
     * @param role            Role of the connection line (or null if it should be automatically created)
     * @param drawableElement Element to connect with
     * @return Role of the connection
     * @throws LineException
     */
    abstract public Role connect(final Role role, final DrawableElement drawableElement) throws LineException;

    /**
     * Returns if this element can be connected with a given object
     *
     * @param drawableElement Object to connect with
     * @return boolean to indicate if connection is allowed
     */
    abstract public boolean canBeConnectedTo(final DrawableElement drawableElement);

    /**
     * Disconnect this element with the given element
     *
     * @param role            Role belonging to the connection
     * @param drawableElement Element to disconnect with
     * @throws LineException
     */
    abstract public void disconnect(final Role role, final DrawableElement drawableElement) throws LineException;

    /**
     * Move this element to a given location (used for dragging)
     *
     * @param newX New X-coordinate
     * @param newY New Y-coördinate
     */
    abstract protected void updateLocation(final int newX, final int newY);

}
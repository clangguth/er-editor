package Shapes;

import Exceptions.LineException;
import Reps.Element;
import Reps.Role;
import UI.Constants;
import UI.ERDiagram;
import UI.EREditor;
import UI.Panels.LinePanel;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * <p>Title: Line</p>
 * <p>Description: A class representing a visible line</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 20/06/2003
 */
public class Line extends DrawableElement {

    /* Propery panel for lines */
    private static LinePanel fPanel = new LinePanel();

    /* Internal connection (only for attributed element) */
    private Role fRole;

    /* Visible line */
    private Line2D fLine = new Line2D.Double();

    /**
     * Constructor
     *
     * @param diagram          Diagram
     * @param role             Internal connection
     * @param drawableElement1 First connected element
     * @param drawableElement2 Second connected element
     */
    public Line(ERDiagram diagram, Role role, DrawableElement drawableElement1, DrawableElement drawableElement2) {
        super(diagram, 0, 0);
        if (drawableElement1 instanceof Relationship) {
            fShape1 = drawableElement1;
            fShape2 = drawableElement2;
        } else {
            fShape2 = drawableElement1;
            fShape1 = drawableElement2;
        }
        fRole = role;
        updateLocation(0, 0);
    }


    private DrawableElement fShape1;
    private DrawableElement fShape2;
    private double fAngle;
    private double fLength;
    private Point2D.Double fArcPoint;

    /**
     * Returns the 'fill color' for a line (based on the colors in Constants.java)
     *
     * @return fill color
     */
    protected Color getColor() {
        return Constants.kLineColor;
    }

    /**
     * Update the position field of the internal element (done before writing to a file)
     */
    public void updateRepPosition() {
        return;
    }

    /**
     * Shows the property panel for a line
     *
     * @param diagram Diagram
     */
    public void showPanel(ERDiagram diagram) {
        EREditor editor = diagram.getEditor();
        if (fRole != null) {
            fPanel.update(diagram, this);
            editor.showPanel(fPanel);
        }
    }

    /**
     * Move this line to a given location (used for dragging)
     *
     * @param newX New X-coordinate
     * @param newY New Y-coördinate
     */
    protected void updateLocation(int newX, int newY) {
        fLine.setLine(fShape1.getCenterX(), fShape1.getCenterY(), fShape2.getCenterX(), fShape2.getCenterY());
        fLength = fLine.getP1().distance(fLine.getP2());
        fAngle = Math.atan2(fLine.getY2() - fLine.getY1(), fLine.getX2() - fLine.getX1());
        fAngle = Math.toDegrees(fAngle);
        if (fAngle < 0) fAngle = fAngle + 360;

    /* Calculate connection points between this line and the elements */
        if (fRole != null) {
            int recursiveLine = fRole.getRecursiveType();
            if (recursiveLine != Role.kNormal) {
        /* Recursive lines */
                double angleValue1 = fShape1.getHeight() / 2 - 4.0;
                double angleValue2 = fShape2.getHeight() / 2 - 4.0;
                Point2D.Double p1 = new Point2D.Double(fLine.getX1() + angleValue1 * Math.cos(Math.toRadians(fAngle + recursiveLine)), fLine.getY1() + angleValue1 * Math.sin(Math.toRadians(fAngle + recursiveLine)));
                Point2D.Double p2 = new Point2D.Double(fLine.getX2() + angleValue2 * Math.cos(Math.toRadians(fAngle + recursiveLine)), fLine.getY2() + angleValue2 * Math.sin(Math.toRadians(fAngle + recursiveLine)));
                fLine.setLine(p1, p2);
            }
        }

    /* Calculate location of the (possible) referential integrity arrow */
        double maxLength;
        if ((fAngle > 35 && fAngle < 47) || (fAngle > 133 && fAngle < 145) || (fAngle > 215 && fAngle < 227) || (fAngle > 313 && fAngle < 325)) {
            maxLength = 160;
        } else if ((fAngle <= 35 || fAngle >= 325) || (fAngle >= 145 && fAngle <= 215)) {
            maxLength = 210;
        } else {
            maxLength = 110;
        }

        double t;
        if (fLength > maxLength) t = 0.7;
        else {
            t = 0.7 / (1 / (fLength / maxLength));
        }

        double a = (1 - t) * fLine.getX1() + t * fLine.getX2();
        double b = (1 - t) * fLine.getY1() + t * fLine.getY2();
        fArcPoint = new Point2D.Double(a, b);
    }

    /**
     * Throws an exception, because nothing can be connected to a line
     *
     * @param role            Role of the connection line (or null if it should be automatically created)
     * @param drawableElement Element to connect with
     * @return Role of the connection
     * @throws LineException These elements cannot be connected
     */
    public Role connect(Role role, DrawableElement drawableElement) throws LineException {
        throw new LineException("These elements cannot be connected...");
    }

    /**
     * Returns always false because nothing can be connected to a line
     *
     * @param drawableElement Object to connect with
     * @return false
     */
    public boolean canBeConnectedTo(DrawableElement drawableElement) {
        return false;
    }

    /**
     * Disconnect this attribute with the given element
     *
     * @param role            Role belonging to the connection
     * @param drawableElement Element to disconnect with
     * @throws LineException
     */
    public void disconnect(Role role, DrawableElement drawableElement) throws LineException {
        return;
    }

    /**
     * Returns if a given point lies inside along this line
     *
     * @param testX X-coördinate
     * @param testY Y-coördinate
     * @return boolean to indicate if the point lies along this line
     */
    public boolean inside(int testX, int testY) {
        return (fLine.ptSegDist(testX, testY) < 5.0);
    }

    /**
     * Returns the center X-coördinate of this line
     *
     * @return center-x
     */
    public int getCenterX() {
        return (int) Math.abs(fLine.getX2() + fLine.getX1()) / 2;
    }

    /**
     * Returns the center Y-coordinate of this line
     *
     * @return center-y
     */
    public int getCenterY() {
        return (int) Math.abs(fLine.getY2() + fLine.getY1()) / 2;
    }

    /**
     * Returns the first drawable element connected to this line
     *
     * @return first connected element
     */
    public DrawableElement getShape1() {
        return fShape1;
    }

    /**
     * Returns the second drawable element connected to this line
     *
     * @return second connected element
     */
    public DrawableElement getShape2() {
        return fShape2;
    }

    /**
     * Returns the visible shape, belonging to a line (Line)
     *
     * @return visible shape
     */
    protected Shape getShape() {
        return fLine;
    }

    /**
     * Returns the internal element, belonging to a line (Role)
     *
     * @return internal element
     */
    public Element getRep() {
        return fRole;
    }

    /**
     * Returns an empty list because a line doesn't have handles
     *
     * @return empty list
     */
    protected ArrayList getHandles() {
        return new ArrayList();
    }

    /**
     * Not used for a line
     *
     * @param diagram Diagram
     */
    public void adjustWidthToName(ERDiagram diagram) {
        return;
    }

    /**
     * Draws this line on the diagram
     *
     * @param g2    Place to draw on
     * @param color Drawing color (normal or selection color)
     */
    public void draw(Graphics2D g2, Color color) {
        updateLocation(0, 0);
        g2.setColor(color);

        if (fRole == null) {
      /* Normal connection line (no cardinality, no role) */
            g2.draw(fLine);
        } else {
            if (fRole.isWeak()) {
        /* Weak role */
                drawWeakLine(g2);
            } else {
        /* Normal role with cardinality */
                g2.draw(fLine);
                drawCardinality(g2);
            }
            if (fRole.getRefIntegrity()) {
        /* Referential integrity arrow */
                drawRefIntegrityArrow(g2);
            }
            if (fRole.getName() != "") {
        /* Role name */
                drawRoleName(g2);
            }
        }
    }

    /**
     * Returns if this line has the given element as an endpoint
     *
     * @param drawableElement Element to test
     * @return boolean to indicate if  this line has the given element as an endpoint
     */
    public DrawableElement isConnectedTo(DrawableElement drawableElement) {
        if (drawableElement == fShape1) {
            return fShape2;
        } else if (drawableElement == fShape2) {
            return fShape1;
        }
        return null;
    }

    /**
     * Draws a referential integrity arrow
     *
     * @param g2 Place to draw on
     */
    private void drawRefIntegrityArrow(Graphics2D g2) {
        double arcRad = 12.5;
        Arc2D.Double arc = new Arc2D.Double(fArcPoint.getX() - arcRad, fArcPoint.getY() - arcRad, arcRad * 2, arcRad * 2, 270 - fAngle, 180, Arc2D.OPEN);
        g2.draw(arc);
    }

    /**
     * Draw the cardinality of the role
     *
     * @param g2 Place to draw on
     */
    private void drawCardinality(Graphics2D g2) {
        g2.drawString(fRole.getCardinality(), getCenterX(), getCenterY());
    }

    /**
     * Draws the name of the role
     *
     * @param g2 Place to draw on
     */
    private void drawRoleName(Graphics2D g2) {
        g2.drawString(fRole.getName(), (int) fArcPoint.x, (int) fArcPoint.y);
    }

    /**
     * Draws two lines (for a weak role)
     *
     * @param g2 Place to draw on
     */
    private void drawWeakLine(Graphics2D g2) {
    /* Line 1 */
        Point2D.Double p1 = new Point2D.Double(fLine.getX1() + 2 * Math.cos(Math.toRadians(fAngle + 90)), fLine.getY1() + 2 * Math.sin(Math.toRadians(fAngle + 90)));
        Point2D.Double p2 = new Point2D.Double(fLine.getX2() + 2 * Math.cos(Math.toRadians(fAngle + 90)), fLine.getY2() + 2 * Math.sin(Math.toRadians(fAngle + 90)));
        g2.draw(new Line2D.Double(p1, p2));
    /* Line 2 */
        Point2D.Double p3 = new Point2D.Double(fLine.getX1() + 2 * Math.cos(Math.toRadians(fAngle - 90)), fLine.getY1() + 2 * Math.sin(Math.toRadians(fAngle - 90)));
        Point2D.Double p4 = new Point2D.Double(fLine.getX2() + 2 * Math.cos(Math.toRadians(fAngle - 90)), fLine.getY2() + 2 * Math.sin(Math.toRadians(fAngle - 90)));
        g2.draw(new Line2D.Double(p3, p4));
    }

}
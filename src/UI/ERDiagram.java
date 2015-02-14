package UI;

import Exceptions.LineException;
import Mapping.Relation;
import Reps.*;
import Shapes.*;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * <p>Title: ERDiagram</p>
 * <p>Description: A class representing an internal Entity Relationship Diagram</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 22/06/2003
 */
public class ERDiagram extends JPanel implements MouseListener, MouseMotionListener {

    /* List with drawable elements (all the visible elements: lines, entities, relationships, ...) */
    private ArrayList fDrawableElements = new ArrayList();

    /* List with selected elements */
    private ArrayList fPickedElements = new ArrayList();

    /* Parent elements (editor, diagramframe) */
    private EREditor fEditor;
    private ERDiagramFrame fDiagramFrame;

    /* Scroll area: area to draw object in */
    private Dimension fArea;

    /* Line to connect two elements (action = 'kDrawingLine') */
    private Line2D fLine;

    /* Current action (drawing line, selecting, dragging, none) */
    private int fAction;

    /* Elements at the bounds of a selection */
    private DrawableElement fLeftElement, fRightElement, fTopElement, fBottomElement;

    /* Rectangle to select multiple elements (action = 'kSelecting') */
    private Rectangle2D fSelection;

    /* Helper values dragging objects (action = 'kDragging') */
    private int fDownX, fDownY;
    private int fStartX, fStopX, fStartY, fStopY;
    private int fPrevDragX, fPrevDragY;

    /* First element when connecting two elements */
    private DrawableElement fConnectedElement;

    /* Relations of this diagram */
    private ArrayList fRelations = new ArrayList();

    /* Value to indicate if the diagram has been changed */
    private boolean fIsUnedited;

    /**
     * Constructor
     *
     * @param editor Editor to which this diagram belongs
     * @param frame  Diagram Frame in which this diagram is drawn
     */
    public ERDiagram(final EREditor editor, final ERDiagramFrame frame) {
        super();
        fEditor = editor;
        fDiagramFrame = frame;
        addMouseListener(this);
        addMouseMotionListener(this);
        init();
    }

    /**
     * Initialization of this diagram
     */
    private void init() {
        fDrawableElements.clear(); /* Elements list */
        fPickedElements.clear(); /* Selected elements list */
        fConnectedElement = null; /* First element when connecting two elements */
        fLine = null; /* Line to connect two elements */
        fSelection = new Rectangle2D.Double(); /* Selection rectangle for multiple selection */
        fArea = new Dimension(0, 0); /* Drawing area */
        setUnedited(true); /* Diagram has not yet been altered */
        fAction = Constants.kNone; /* Current action */
    }

    /**
     * Returns if the diagram has been altered
     *
     * @return boolean to indicate if the diagram has been altered
     */
    public boolean isUnedited() {
        return fIsUnedited;
    }

    /**
     * Report that the diagram has been changed
     *
     * @param unedited Boolean to indicate if the diagram has been changed
     */
    public void setUnedited(final boolean unedited) {
        fIsUnedited = unedited;
        if (unedited) {
            fDiagramFrame.setTitle(fDiagramFrame.getName());
        } else {
      /* Indication of a - not saved - altered diagram is done by the '*' mark */
            fDiagramFrame.setTitle(fDiagramFrame.getName() + "*");
        }
    }

    /**
     * Add an element (entity, ISA, line, ...) to this diagram
     *
     * @param element Element to add
     */
    public void add(final DrawableElement element) {
        if (element instanceof Line) {
      /* Lines have to be drawn before elements, so we add lines in front of the list */
            fDrawableElements.add(0, element);
        } else {
      /* Other elements are added at the back of the list */
            fDrawableElements.add(element);
        }
        adjustPanelToSelection();
    }

    /**
     * Add an element (entity, ISA, line, ...) to the diagram and report this to the undo-function
     *
     * @param element Element to add
     */
    public void addWithUndo(final DrawableElement element) {
        UndoableEdit edit = new ActionAdd(this, element);
        add(element);
        fEditor.fUndoSupport.postEdit(edit);
    }

    /**
     * Adjust the diagram draw area to the used space and report this to the undo-function
     */
    public void adjustFrameSizeWithUndo() {
        UndoableEdit edit = new ActionAdjustFrameSize(this, fArea.width, fArea.height);
        adjustFrameSize();
        fEditor.fUndoSupport.postEdit(edit);
    }

    /**
     * Remove an element from this diagram and report this to the undo-function
     */
    public void deleteWithUndo() {
        UndoableEdit edit = new ActionDelete(this, fPickedElements);
        deleteSelection();
        fEditor.fUndoSupport.postEdit(edit);
    }

    /**
     * Translate an element x, y points
     *
     * @param x X-coordinate of the translation
     * @param y Y-coördinate of the translation
     */
    private void translateWithUndo(final int x, final int y) {
        UndoableEdit edit = new ActionTranslate(this, fPickedElements, x, y);
        fEditor.fUndoSupport.postEdit(edit);
    }

    /**
     * Checks if this diagram is valid
     *
     * @return string with the reason why this attribute is not OK (empty if there are no errors)
     */
    public String check() {
        String reason = "";
    /* Check all the elements to see if they are valid */
        Iterator itDrawableElements = fDrawableElements.iterator();
        while (itDrawableElements.hasNext()) {
            Element element = ((DrawableElement) itDrawableElements.next()).getRep();
            if (element != null) {
                String str = element.check();
                if (str != null) reason += "\n" + str;
            }
        }
    /* To prevent a box larger than our screen height */
        if (reason.length() > 500) reason = "To many errors.";
        return reason;
    }

    /**
     * Remove an element from this diagram
     *
     * @param element Element to remove
     */
    public void delete(final DrawableElement element) {
        try {
            if (element instanceof Line) {
                Line line = (Line) element;
                Role role = (Role) element.getRep();
                line.getShape1().disconnect(role, line.getShape2());
            } else {
        /* Remove connected lines to this element */
                Iterator itLines = element.getConnectedLinesWith().iterator();
                while (itLines.hasNext()) {
                    Line line = (Line) itLines.next();
                    element.disconnect((Role) line.getRep(), line.isConnectedTo(element));
                    fDrawableElements.remove(line);
                }
            }
      /* Remove the element from the diagram */
            fDrawableElements.remove(element);
      /* Check to see if we were just connecting this element with another */
            if (element == fConnectedElement) {
        /* stop this connecting action */
                fConnectedElement = null;
                fAction = Constants.kNone;
            }
            adjustPanelToSelection();
        } catch (LineException e) {
            fEditor.setStatusMessage(e.getMessage());
        }
    }

    /**
     * Remove all selected elements from this diagram
     */
    public void deleteSelection() {
        Iterator itPickedElements = fPickedElements.iterator();
        while (itPickedElements.hasNext()) {
            DrawableElement element = (DrawableElement) itPickedElements.next();
            itPickedElements.remove();
            delete(element);
        }
        fAction = Constants.kNone;
    }

    /**
     * Calculate the elements at the bounds of the current selection
     */
    private void updateSelectionBounds() {
        fLeftElement = null;
        fTopElement = null;
        fRightElement = null;
        fBottomElement = null;

        Iterator itPickedElements = fPickedElements.iterator();
        while (itPickedElements.hasNext()) {
            DrawableElement element = (DrawableElement) itPickedElements.next();
            if (!(element instanceof Line)) {

      /* Most left element of the selection */
                if (fLeftElement == null || element.getLeft() < fLeftElement.getLeft()) {
                    fLeftElement = element;
                }
      /* Most upper element of the selection */
                if (fTopElement == null || element.getTop() < fTopElement.getTop()) {
                    fTopElement = element;
                }
      /* Most right element of the selection */
                if (fRightElement == null || element.getRight() > fRightElement.getRight()) {
                    fRightElement = element;
                }
      /* Bottom element of the selection */
                if (fBottomElement == null || element.getBottom() > fBottomElement.getBottom()) {
                    fBottomElement = element;
                }
            }
        }
    }

    /**
     * Select all elements of this diagram
     */
    public final void selectAll() {
        fPickedElements.clear();
        fPickedElements.addAll(fDrawableElements);
        adjustPanelToSelection();
        fAction = Constants.kNone;
    }

    /**
     * Deselect all the elements of this diagram
     */
    public final void deselectAll() {
        fPickedElements.clear();
        adjustPanelToSelection();
    }

    /**
     * Return the diagram as an image (used for copy-to-clipboard)
     *
     * @return image of the diagram
     */
    public Image getImage() {
        Frame f = new Frame();
        f.addNotify();
        Image img = f.createImage(getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        revalidate();
        paint(g2d);
        return img;
    }

    /**
     * Returns the element in which the given coördinates are located
     *
     * @param x X-coördinate of the location
     * @param y Y-coördinate of the location
     * @return element at the given location
     */
    private DrawableElement findElement(final int x, final int y) {
    /* Start at the back of the list, because that's where the elements are located */
        ListIterator itDrawableElements = fDrawableElements.listIterator(fDrawableElements.size());
        while (itDrawableElements.hasPrevious()) {
            DrawableElement currentElement = (DrawableElement) itDrawableElements.previous();
      /* If the coördinates lie into an element, than return this element */
            if (currentElement.inside(x, y)) return currentElement;
        }
        return null;
    }

    /**
     * Returns the SQL code for this diagram
     *
     * @return sql code
     */
    public String getSQL() {
        ArrayList relationsToDo = new ArrayList();
        relationsToDo.addAll(getMapping());

        String sql = "";
        while (!relationsToDo.isEmpty()) {
            Iterator itRelations = relationsToDo.iterator();
            while (itRelations.hasNext()) {
                Relation relation = (Relation) itRelations.next();
                boolean ok = true;
                ArrayList references = relation.getReferences();
                Iterator itToDo = relationsToDo.iterator();
                while (itToDo.hasNext()) {
                    Relation relationToDo = (Relation) itToDo.next();
                    if (references.contains(relationToDo.getName())) ok = false;
                }
                if (ok) {
                    sql += relation.getSQL();
                    sql += "\n";
                    itRelations.remove();
                }
            }
        }
        return sql;
    }

    /**
     * Returns all the elements of this diagram
     *
     * @return list with elements of this diagram
     */
    public ArrayList getElements() {
        return fDrawableElements;
    }

    /**
     * Returns the mapping of this diagram
     *
     * @return mapping of the diagram
     */
    public ArrayList getMapping() {
        fRelations.clear();
        Iterator itDrawableElements = fDrawableElements.iterator();
        while (itDrawableElements.hasNext()) {
            DrawableElement drawableElement = (DrawableElement) itDrawableElements.next();
            if ((drawableElement.getRep() instanceof AttributedElement)) {
                AttributedElement currentElement = (AttributedElement) drawableElement.getRep();
                Relation newRelation = currentElement.getMappedRelation();
                if (newRelation != null) {
          /* If a relation with this name already exists, keep the relation with the most attributes */
                    Relation oldRelation = getRelation(newRelation.getName());
                    if (oldRelation != null) {
                        if (newRelation.getNrAttributes() > oldRelation.getNrAttributes()) {
                            fRelations.add(newRelation);
                            fRelations.remove(oldRelation);
                        }
                    } else {
                        fRelations.add(newRelation);
                    }
                }
            } else if (drawableElement instanceof Attribute) {
                AttributeRep currentElement = (AttributeRep) drawableElement.getRep();
                Relation newRelation = currentElement.getMappedRelation();
                if (newRelation != null) fRelations.add(newRelation);
            }
        }
        return fRelations;
    }

    /**
     * Returns the relation with the given name
     *
     * @param name Name of the relation we want to get
     * @return relation
     */
    private Relation getRelation(final String name) {
        Iterator itRelations = fRelations.iterator();
        while (itRelations.hasNext()) {
            Relation relation = (Relation) itRelations.next();
            if (relation.getName() == name) return relation;
        }
        return null;
    }

    /**
     * Deselect an element in this diagram
     *
     * @param element Element to deselect
     */
    protected void deselect(final DrawableElement element) {
        fPickedElements.remove(element);
        adjustPanelToSelection();
    }

    /**
     * Select an element in this diagram
     *
     * @param element          Element to select
     * @param deselectAllFirst Indicate if we want to deselect all the elements first
     */
    private void select(final DrawableElement element, final boolean deselectAllFirst) {
        if (deselectAllFirst) deselectAll();
        fPickedElements.add(element);
        adjustPanelToSelection();
    }

    /**
     * Select all the elements in the selected area (in the selection rectangle)
     *
     * @param selection Selection rectangle
     */
    public void select(final Rectangle2D selection) {
        fPickedElements.clear();

        Iterator itDrawableElements = fDrawableElements.iterator();
        while (itDrawableElements.hasNext()) {
            DrawableElement currentElement = (DrawableElement) itDrawableElements.next();
            if (selection.contains(currentElement.getCenterX(), currentElement.getCenterY())) {
                fPickedElements.add(currentElement);
            }
        }
    }

    /**
     * Paints the diagram with all its elements
     *
     * @param g Place to paint on
     */
    public void paint(final Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paint(g);

    /* Smooth drawing of the elements */
        if (fEditor.isSmoothDrawing()) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

    /* Action = selecting multiple elements => drawing selection rectangle */
        if (fAction == Constants.kSelecting) {
            g2.setColor(Constants.kSelectionColor);
            g2.draw(fSelection);
        }

    /* Action = connecting two elements => draw connection line */
        if (fAction == Constants.kDrawingLine) {
            g2.setColor(Color.black);
            g2.draw(fLine);
            drawPossibleConnections(g2);
        } else {
      /* Draw all the elements of this diagram */
            Iterator itDrawableElements = fDrawableElements.iterator();
            while (itDrawableElements.hasNext()) {
                DrawableElement element = (DrawableElement) itDrawableElements.next();
                if (fPickedElements.contains(element)) {
          /* Element is selected */
                    element.draw(g2, Constants.kSelectionColor);
                    element.drawHandles(g2);
                } else {
          /* Element is not selected */
                    element.draw(g2, Color.black);
                }
            }
        }
    }

    /**
     * Paints all the elements to which we could connect an element (fConnectedElement)
     *
     * @param g2 Place to paint on
     */
    private void drawPossibleConnections(final Graphics2D g2) {
        Iterator itDrawableElements = fDrawableElements.iterator();
        while (itDrawableElements.hasNext()) {
            DrawableElement el = (DrawableElement) itDrawableElements.next();
            if (fConnectedElement.canBeConnectedTo(el) || !fEditor.isIntelligentDrawing()) {
                el.draw(g2, Color.black);
            } else {
                el.draw(g2, Color.lightGray);
            }
        }
    }

    /**
     * Returns the editor to which this diagram belongs
     *
     * @return Editor
     */
    public EREditor getEditor() {
        return fEditor;
    }

    /**
     * Listener on clicking the mouse button
     *
     * @param e Mouse event
     */
    public void mouseClicked(final MouseEvent e) {
    }

    /**
     * Listener on pressing the mouse button
     *
     * @param e Mouse event
     */
    public void mousePressed(final MouseEvent e) {

    /* Click with the right mouse button => cancel current action */
        if (e.getButton() != e.BUTTON1) {
            fAction = Constants.kNone;
            repaint();
            return;
        }

        int x = e.getX();
        int y = e.getY();

    /* Helper values for dragging elements */
        fDownX = x;
        fDownY = y;
        fStartX = x;
        fStartY = y;
        fStopX = x;
        fStopY = y;
        fPrevDragX = x;
        fPrevDragY = y;

        int drawObject = fEditor.getCurrentDrawObject();

        if (fAction != Constants.kDrawingLine) {
            DrawableElement newElement = null;
            switch (drawObject) {
                case Constants.kEdit:
          /* Editing */
                    DrawableElement element = findElement(x, y);
                    if (element != null) {
            /* Select an element */
                        if (!fPickedElements.contains(element)) {
                            select(element, true);
                        }
            /* Prepare for draggin */
                        fAction = Constants.kDragging;
                        repaint();
                    } else {
                        deselectAll();
                        fAction = Constants.kSelecting;
                    }
                    updateSelectionBounds();
                    return;
                case Constants.kEntity:
          /* Add an entity to the diagram */
                    newElement = new Entity(this, new EntityRep(), x, y);
                    break;
                case Constants.kAttribute:
          /* Add an attribute to the diagram */
                    newElement = new Attribute(this, new AttributeRep(), x, y);
                    if (fEditor.isIntelligentDrawing()) {
            /* Draw a connection line to connect this attribute with another element */
                        fConnectedElement = newElement;
                        fLine = new Line2D.Double();
                        fAction = Constants.kDrawingLine;
                    }
                    break;
                case Constants.kRelationship:
          /* Add a relationship to the diagram */
                    newElement = new Relationship(this, new RelationshipRep(), x, y);
                    break;
                case Constants.kISA:
          /* Add an ISA Relationship to the diagram */
                    newElement = new ISA(this, new ISARep(), x, y);
                    break;
            }

            if (newElement != null) {
                addWithUndo(newElement);
                select(newElement, true);
                updateFrameSize(x, y);
                return;
            }
        }

        DrawableElement connectedElement = findElement(x, y);
        if (connectedElement != null && !(connectedElement instanceof Line)) {
            if (fAction == Constants.kDrawingLine) {
        /* End connecting two elements */
                fAction = Constants.kNone;
                fLine = null;
                try {
                    Role role = fConnectedElement.connect(null, connectedElement);
                    Line line = new Line(this, role, fConnectedElement, connectedElement);
                    addWithUndo(line);
                } catch (LineException e2) {
                    fEditor.setStatusMessage(e2.getMessage());
                }
            } else {
        /* Begin connecting two elements */
                fConnectedElement = connectedElement;
                fAction = Constants.kDrawingLine;
                fLine = new Line2D.Double();
            }
            repaint();
        }
    }

    /**
     * Listener on moving the mouse
     *
     * @param e Mouse event
     */
    public void mouseMoved(final MouseEvent e) {
        if (fAction == Constants.kDrawingLine) {
            fLine.setLine(fConnectedElement.getCenterX(), fConnectedElement.getCenterY(), e.getX(), e.getY());
            repaint();
        }
    }

    /**
     * Listener on dragging the mouse
     *
     * @param e Mouse event
     */
    public void mouseDragged(final MouseEvent e) {

        if (fAction == Constants.kNone) return;

        int x = e.getX();
        int y = e.getY();

        if (fPickedElements.isEmpty() || fAction == Constants.kSelecting) {
      /* Multiple element selection */
            int x1, x2, y1, y2;
            if (x < fDownX) {
                x1 = x;
                x2 = fDownX;
            } else {
                x1 = fDownX;
                x2 = x;
            }
            if (y < fDownY) {
                y1 = y;
                y2 = fDownY;
            } else {
                y1 = fDownY;
                y2 = y;
            }
            fSelection.setFrame(x1, y1, x2 - x1, y2 - y1);
            select(fSelection);
            repaint();
            return;
        }

        if (fEditor.getCurrentDrawObject() == Constants.kEdit) {
            if ((fPickedElements.size() == 1) && (fPickedElements.get(0) instanceof Line)) {
        /* Lines cannot be dragged */
                return;
            }

      /* Dragging elements */
            int minX = fLeftElement.getLeft();
            int minY = fTopElement.getTop();
            int maxX = fRightElement.getRight();
            int maxY = fBottomElement.getBottom();

            int transX, transY;

      /* Checks to see that we don't draw out of the frame (left or up) */
            if (minX + (x - fPrevDragX) <= 0) {
                if (x < fPrevDragX) transX = 0;
                else transX = x - fPrevDragX;
            } else {
                transX = x - fPrevDragX;
            }

            if (minY + (y - fPrevDragY) <= 0) {
                if (y < fPrevDragY) transY = 0;
                else transY = y - fPrevDragY;
            } else {
                transY = y - fPrevDragY;
            }

            updateFrameSize(maxX + transX, maxY + transY);
            Iterator itPickedElements = fPickedElements.iterator();
            while (itPickedElements.hasNext()) {
                DrawableElement currentElement = (DrawableElement) itPickedElements.next();
                currentElement.translate(transX, transY);
            }
            fStopX += transX;
            fStopY += transY;

            Rectangle rect = new Rectangle(x, y, 0, 0);
            scrollRectToVisible(rect);
            fPrevDragX = x;
            fPrevDragY = y;
        }
    }

    /**
     * Listener on releasing the mouse button
     *
     * @param e Mouse event
     */
    public void mouseReleased(final MouseEvent e) {
        if (fAction == Constants.kSelecting) {
      /* Done selecting */
            repaint();
            fAction = Constants.kNone;
            adjustPanelToSelection();
        } else if (fAction == Constants.kDragging) {
      /* Done dragging => report translation to the undo-function */
            if (fStopX != fStartX || fStopY != fStartY) {
                translateWithUndo(fStopX - fStartX, fStopY - fStartY);
            }
            fAction = Constants.kNone;
        }
    }

    /**
     * Updates the drawing area when dragging elements out of the frame
     *
     * @param x X-coördinate (minimal width of the new area)
     * @param y Y-coorindate (minimal height of the new area)
     */
    public void updateFrameSize(final int x, final int y) {
        boolean changed = false;

    /* If necessary, enlarge drawing area */
        if (x > fArea.width) {
            fArea.width = x;
            changed = true;
        }

        if (y > fArea.height) {
            fArea.height = y;
            changed = true;
        }

        if (changed) {
            setPreferredSize(fArea);
            revalidate();
        }
        repaint();
    }

    /**
     * Adjust the diagram draw area to the used space
     */
    public void adjustFrameSize() {
        int maxX = 0;
        int maxY = 0;
        int x, y;

    /* Calculate bounds */
        Iterator itElements = fDrawableElements.iterator();
        while (itElements.hasNext()) {
            DrawableElement element = (DrawableElement) itElements.next();
            x = element.getRight();
            y = element.getBottom();

            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

    /* Adjust drawing area to this bounds */
        fArea.width = maxX + 10;
        fArea.height = maxY + 10;
        setPreferredSize(fArea);
        revalidate();
    }

    /**
     * Listener on entering a mouse action
     *
     * @param e Mouse event
     */
    public void mouseEntered(final MouseEvent e) {

    }

    /**
     * Listener on exiting a mouse action
     *
     * @param e Mouse event
     */
    public void mouseExited(final MouseEvent e) {
    }

    /**
     * Writes the XML rules for this diagram to the write buffer
     *
     * @param out Place to write to
     * @throws IOException
     */
    public void write(final Writer out) throws IOException {

        out.write("<?xml version='1.0'?>\n");
        out.write("<diagram>\n");
        out.write("\t<elements>\n");

    /* Write elements */
        Iterator itDrawableElements = fDrawableElements.iterator();
        while (itDrawableElements.hasNext()) {
            DrawableElement drawableElement = ((DrawableElement) itDrawableElements.next());
            drawableElement.updateRepPosition();
            Element element = drawableElement.getRep();
            if (element != null && !(element instanceof Role)) {
                element.write(out);
            }
        }

        out.write("\t</elements>\n");

    /* Write references (additional information) to the elements */
        itDrawableElements = fDrawableElements.iterator();
        while (itDrawableElements.hasNext()) {
            DrawableElement drawableElement = ((DrawableElement) itDrawableElements.next());
            Element element = drawableElement.getRep();
            if (element != null && !(element instanceof AttributeRep)) {
                element.writeReference(out);
            }
        }

        out.write("</diagram>\n");
        out.flush();
    }

    /**
     * Adjust the property panel to the selected element (and hide it if multiple elements are selected)
     */
    private void adjustPanelToSelection() {
        if (fPickedElements.size() == 1) {
      /* Only one element is selected => show property panel */
            DrawableElement element = (DrawableElement) fPickedElements.get(0);
            element.showPanel(this);
        } else {
      /* None or multiple elements are selected => hide property panel */
            fEditor.hidePanel();
        }
    }
}

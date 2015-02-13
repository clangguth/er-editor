package UI;

import java.awt.*;

/**
 * <p>Title: Constants</p>
 * <p>Description: Program Constants</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 14/06/2003
 */
public class Constants {

    /* Selection color */
    public static final Color kSelectionColor = Color.red;

    /* Element fill colors */
    public static final Color kAttributeColor = Color.decode("#C0C0FF");
    public static final Color kEntityColor = Color.decode("#C0FFC0");
    public static final Color kISAColor = Color.decode("#FFC0FF");
    public static final Color kRelationshipColor = Color.decode("#FFFFC0");
    public static final Color kLineColor = Color.black;

    /* Element sizes */
    public static final int kAttributeWidth = 80;
    public static final int kAttributeHeight = 34;
    public static final int kEntityWidth = 90;
    public static final int kEntityHeight = 30;
    public static final int kISAWidth = 60;
    public static final int kISAHeight = 40;
    public static final int kRelationshipWidth = 80;
    public static final int kRelationshipHeight = 50;

    /* Element numbers */
    public static final int kEdit = 0;
    public static final int kEntity = 1;
    public static final int kAttribute = 2;
    public static final int kRelationship = 3;
    public static final int kISA = 4;
    public static final int kLine = 5;

    /* Diagram actions */
    public static final int kNone = 0;
    public static final int kSelecting = 1;
    public static final int kDrawingLine = 2;
    public static final int kDragging = 3;

    /* Data Types (SQL) */
    public static final String[] kTypeArray = {"char", "varchar", "date", "integer", "float", "serial"};
    public static final String[] kTypeWithLength = {"char", "varchar"};
    public static final String kTypeDefault = "varchar";
}
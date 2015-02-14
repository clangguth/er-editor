package Mapping;

import Reps.AttributeRep;
import UI.Constants;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: Relation</p>
 * <p>Description: Used for the mapping to the Relational Model and for SQL Generation</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 22/06/2003
 */
public class Relation {

    /* Name of the relation */
    private String fName;

    /* Key attributes */
    private final ArrayList fKeyAttributes;

    /* Non-key attributes */
    private ArrayList fNonKeyAttributes;

    /* Boolean to indicate if this relation comes from a relationship */
    private boolean fIsRelationship;

    /**
     * Constructor
     *
     * @param name           Name of the relation
     * @param isRelationship Boolean to indicate if this comes from a relationship
     */
    public Relation(final String name, final boolean isRelationship) {
        fName = name;
        fKeyAttributes = new ArrayList();
        fNonKeyAttributes = new ArrayList();
        fIsRelationship = isRelationship;
    }

    /**
     * Add a key attribute
     *
     * @param attribute Attribute to add
     */
    public void addKeyAttribute(final AttributeRep attribute) {
        fKeyAttributes.add(attribute);
    }

    /**
     * Add a non-key attribute
     *
     * @param attribute Attribute to add
     */
    public void addNonKeyAttribute(final AttributeRep attribute) {
        fNonKeyAttributes.add(attribute);
    }

    /**
     * Add multiple key attributes
     *
     * @param attributes List with key attributes
     */
    public void addAllKeyAttributes(final ArrayList attributes) {
        fKeyAttributes.addAll(attributes);
    }

    /**
     * Add multiple non-key attributes
     *
     * @param attributes List with non-key attributes
     */
    public void addAllNonKeyAttributes(final ArrayList attributes) {
        fNonKeyAttributes.addAll(attributes);
    }

    /**
     * Returns a list with key attributes
     *
     * @return List with key attributes
     */
    public ArrayList getKeyAttributes() {
        return fKeyAttributes;
    }

    /**
     * Returns a list with non-key attributes
     *
     * @return List with non-key attributes
     */
    public ArrayList getNonKeyAttributes() {
        return fNonKeyAttributes;
    }

    /**
     * Returns the relation name
     *
     * @return Name of the relation
     */
    public String getName() {
        return fName;
    }

    /**
     * Returns the references from this relation
     *
     * @return References list
     */
    public ArrayList getReferences() {
        ArrayList attributes = new ArrayList();
        attributes.addAll(fKeyAttributes);
        attributes.addAll(fNonKeyAttributes);

        ArrayList references = new ArrayList();
        Iterator itAttributes = attributes.iterator();
        while (itAttributes.hasNext()) {
            AttributeRep attr = (AttributeRep) itAttributes.next();
            String name = attr.getAttributedElement().getName();
            if (!name.equals(fName) && !references.contains(name)) references.add(name);
        }
        return references;
    }

    /**
     * Returns the SQL code for this relation
     *
     * @return SQL code
     */
    public String getSQL() {
        String sql;
        sql = "CREATE TABLE " + fName + "(";

        ArrayList attributes = new ArrayList();
        attributes.addAll(fKeyAttributes);
        attributes.addAll(fNonKeyAttributes);

    /* Attributes */
        Iterator itAttributes = attributes.iterator();
        while (itAttributes.hasNext()) {
            sql += "\n";
            AttributeRep attr = (AttributeRep) itAttributes.next();
            String name;
            if (alreadyExists(attr)) {
                name = attr.getAttributedElement().getName() + "_" + attr.getName();
            } else {
                name = attr.getName();
            }
            sql += " " + name + " " + attr.getDataType();

            boolean showLength = false;
            for (int i = 0; i < Constants.kTypeWithLength.length; i++) {
                if (Constants.kTypeWithLength[i].equals(attr.getDataType())) showLength = true;
            }

            if (showLength) {
                sql += "(" + attr.getLength() + ")";
            }

            if (!fIsRelationship) {
                if (attr.isUnique()) sql += " UNIQUE";
                if (attr.isRequired()) sql += " NOT NULL";
            } else {
                sql += " NOT NULL";
            }

            if (itAttributes.hasNext()) {
                sql += ",";
            }
        }

    /* Foreign keys (references) */

        itAttributes = attributes.iterator();
        while (itAttributes.hasNext()) {
            AttributeRep attr = (AttributeRep) itAttributes.next();
            if (!attr.getAttributedElement().getName().equals(fName)) {
                sql += ",\nFOREIGN KEY (";
                sql += attr.getName() + ") REFERENCES " + attr.getAttributedElement().getName() + " (" + attr.getName() + ")";
            }
        }

    /* Primary Key */
        sql += ",\nPRIMARY KEY (";
        itAttributes = fKeyAttributes.iterator();
        while (itAttributes.hasNext()) {
            AttributeRep attr = (AttributeRep) itAttributes.next();
            String name;
            if (alreadyExists(attr)) {
                name = attr.getAttributedElement().getName() + "_" + attr.getName();
            } else {
                name = attr.getName();
            }
            sql += name;
            if (itAttributes.hasNext()) {
                sql += ",";
            }
        }

        sql += "));\n";
        return sql;
    }

    /**
     * Returns the number of attributes for this relation
     *
     * @return Number of attributes
     */
    public int getNrAttributes() {
        return fKeyAttributes.size() + fNonKeyAttributes.size();
    }

    /**
     * Returns if an attribute already exists
     *
     * @param attribute Attribute to test
     * @return Boolean to indicate if an attribute already exists
     */
    public boolean alreadyExists(final AttributeRep attribute) {
    /* Key attributes*/
        Iterator itAttributes = fKeyAttributes.iterator();
        while (itAttributes.hasNext()) {
            AttributeRep attr = (AttributeRep) itAttributes.next();
            if (!attribute.equals(attr) && attribute.getName().equals(attr.getName())) return true;
        }

    /* Non-key attributes */
        itAttributes = fNonKeyAttributes.iterator();
        while (itAttributes.hasNext()) {
            AttributeRep attr = (AttributeRep) itAttributes.next();
            if (!attribute.equals(attr) && attribute.getName().equals(attr.getName())) return true;
        }
        return false;
    }

}
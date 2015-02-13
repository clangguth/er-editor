package Reps;

import Exceptions.AttributeRepException;
import Exceptions.AttributedElementException;
import Mapping.Relation;
import UI.Constants;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>Title: AttributeRep</p>
 * <p>Description: A class representing an internal attribute</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 22/06/2003
 */
public class AttributeRep extends Element {

    public static final String kNormal = "normal";
    public static final String kKey = "key";
    public static final String kMultivalued = "multivalued";

    /* Holds the number of attributes made so far */
    private static int fAttributeRepCount = 0;

    /* Attributed Element to which this attribute belongs */
    private AttributedElement fAttributedElement = null;

    /* Type of the attribute: normal, key or multivalued */
    private String fType = kNormal;

    /* DataType of the attribute (char, serial, date, ...) (SQL) */
    private String fDataType = Constants.kTypeDefault;

    /* Boolean to indicate that this attribute is required (SQL) */
    private boolean fRequired = false;

    /* Boolean to indicate that this attribute is uniqe (SQL) */
    private boolean fUnique = false;

    /* Length of the attribute field (SQL) */
    private int fLength = 50;

    /**
     * Constructor
     *
     * @param name Name of the attribute
     * @param type Type: kNormal, kKey or kMultivalued
     */
    public AttributeRep(final String name, final String type) {
        super(name);
        fType = type;
        fAttributeRepCount++;
    }

    /**
     * Constructor (without a name)
     */
    public AttributeRep() {
        super("Attribute" + (++fAttributeRepCount));
    }

    /**
     * Returns the type of the attribute
     *
     * @return kNormal, kKey or kMultivalued
     */
    public String getType() {
        return fType;
    }

    /**
     * Returns the datatype of the attribute (SQL)
     *
     * @return datatype
     */
    public String getDataType() {
        return fDataType;
    }

    /**
     * Returns if the attribute is unique (SQL)
     *
     * @return boolean to indicate if the attribute is unique
     */
    public boolean isUnique() {
        return fUnique;
    }

    /**
     * Returns the mapped relation of this attribute (null when type != multivalued)
     *
     * @return mapped relation
     */
    public Relation getMappedRelation() {
        if (!fType.equals(kMultivalued) || fAttributedElement == null) return null;
        Relation relation = new Relation(fAttributedElement.getName() + "_" + getName(), false);
        relation.addAllKeyAttributes(fAttributedElement.getPrimaryKey());
        relation.addKeyAttribute(this);
        return relation;
    }

    /**
     * Returns if the attribute is required (SQL)
     *
     * @return boolean to indicate if the attribute is required
     */
    public boolean isRequired() {
        return fRequired;
    }

    /**
     * Returns always false, because an attribute cannot be weak
     *
     * @return false
     */
    public boolean isWeak() {
        return false;
    }

    /**
     * Returns the length of the attribute (SQL)
     *
     * @return length of the attribute
     */
    public int getLength() {
        return fLength;
    }

    /**
     * Sets the type of the attribute
     *
     * @param type Type: kNormal, kKey or kMultivalued
     * @throws AttributeRepException Relationship cannot have key attributes
     * @throws AttributeRepException Sub Entities cannot have key attributes
     * @throws AttributeRepException There are entities dependent on this key attribute
     */
    public void setType(final String type) throws AttributeRepException {
        if (fAttributedElement != null) {
            if (fAttributedElement instanceof RelationshipRep) {
        /* fAttributedElement is a Relationship */
                throw new AttributeRepException("Relationship cannot have key attributes...");
            } else {
        /* fAttributedElement is an Entity */
                EntityRep entity = (EntityRep) fAttributedElement;
                if (entity.isSubEntity() && type.equals(kKey)) {
                    throw new AttributeRepException("Sub Entities cannot have key attributes...");
                } else if (fType.equals(kKey) && entity.givesDependency()) {
                    throw new AttributeRepException("There are entities dependent on this key attribute...");
                }
            }
        }
        if (type == kKey) fRequired = true;
        fType = type;
    }

    /**
     * Sets the required parameter (SQL)
     *
     * @param required Boolean to indicate that this attribute is required
     * @throws AttributeRepException A Key field is always required
     */
    public void setRequired(final boolean required) throws AttributeRepException {
        if (!required && (fType == kKey))
            throw new AttributeRepException("A Key field is always required...");
        fRequired = required;
    }

    /**
     * Sets the unique parameter (SQL)
     *
     * @param unique Boolean to indicate that this field is unique
     */
    public void setUnique(final boolean unique) {
        fUnique = unique;
    }

    /**
     * Set the data type of this attribute (SQL)
     *
     * @param dataType DataType (char, serial, ...)
     */
    public void setDataType(final String dataType) {
        fDataType = dataType;
    }

    /**
     * Set the length of this attribute (SQL)
     *
     * @param length Length of this attribute
     */
    public void setLength(final int length) {
        fLength = length;
    }

    /**
     * Sets the name of this attribute
     *
     * @param name New name
     * @throws AttributeRepException An attribute with the same name is already connected to this element
     */
    public void setName(final String name) throws AttributeRepException {
        String oldName = getName();
        try {
            super.setName(name);
            if (fAttributedElement != null && fAttributedElement.attributeAlreadyExists(this)) {
        /* Name already existed, so reset to previous name */
                super.setName(oldName);
                throw new AttributedElementException("An attribute with the same name is already connected to this element...");
            }
        } catch (Exception e) {
            throw new AttributeRepException(e.getMessage());
        }
    }

    /**
     * Sets the attributed element (entity or relationship) to which this attribute belongs
     *
     * @param attributedElement element to which this attribute belongs
     */
    protected void setAttributedElement(final AttributedElement attributedElement) {
        fAttributedElement = attributedElement;
    }

    /**
     * Returns the attributed element to which this attribute belongs
     *
     * @return element to which this attribute belongs
     */
    public AttributedElement getAttributedElement() {
        return fAttributedElement;
    }

    /**
     * Checks if this attribute is valid
     *
     * @return string with the reason why this attribute is not OK
     */
    public String check() {
        if (fAttributedElement == null) {
            return "Attribute \"" + getName() + "\" not connected to an entity";
        }
        return null;
    }

    /**
     * Writes the XML rule for a reference to this attribute to the write buffer
     *
     * @param out place to write to
     * @throws IOException
     */
    public void writeReference(final Writer out) throws IOException {
    }

    /**
     * Writes the XML rule for this attribute to the write buffer
     *
     * @param out place to write to
     * @throws IOException
     */
    public void write(final Writer out) throws IOException {
        out.write("\t\t<attributeid id=\"" + getID() + "\" posX=\"" + getX() + "\" posY=\"" + getY() + "\" type=\"" + fType + "\" name=\"" + getName() + "\" required=\"" + fRequired + "\" unique=\"" + fUnique + "\" datatype=\"" + fDataType + "\" length=\"" + fLength + "\">");
        out.write("</attributeid>\n");
    }
}
package Reps;

import Exceptions.AttributedElementException;
import Mapping.Relation;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: AttributedElement</p>
 * <p>Description: A base class representing an internal attributed element (Entity or Relationship)</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 19/06/2003
 */
abstract public class AttributedElement extends Element {

    /* ArrayList with all the attributes of this attributed element */
    public ArrayList fAttributes = new ArrayList();

    /**
     * Constructor
     *
     * @param name Name of the attributed element
     */
    public AttributedElement(final String name) {
        super(name);
    }

    /**
     * Add an attribute to this attributed element
     *
     * @param attribute Attribute to add
     * @throws AttributedElementException Attribute already belongs to this element
     * @throws AttributedElementException Sub Entities cannot have key attributes
     * @throws AttributedElementException Attribute already belongs to another element
     * @throws AttributedElementException An attribute with the same name is already connected to this element
     */
    public void addAttribute(final AttributeRep attribute) throws AttributedElementException {
        if (fAttributes.contains(attribute)) {
            throw new AttributedElementException("Attribute already belongs to this element...");
        } else if (isSubEntity() && attribute.getType().equals(AttributeRep.kKey)) {
            throw new AttributedElementException("Sub Entities cannot have key attributes...");
        } else if (attribute.getAttributedElement() != null) {
            throw new AttributedElementException("Attribute already belongs to another element...");
        } else if (attributeAlreadyExists(attribute)) {
            throw new AttributedElementException("An attribute with the same name is already connected to this element...");
        }
        fAttributes.add(attribute);
        attribute.setAttributedElement(this);
    }

    /**
     * Returns if an attribute can be added to this attributed element
     *
     * @param attribute Attribute of which we want to know if it can be added to this attributed element
     * @return boolean to indicate if the attribute can be added
     */
    public boolean canAddAttribute(final AttributeRep attribute) {
        if (fAttributes.contains(attribute)) {
      /* Attribute already belongs to this element */
            return false;
        } else if (isSubEntity() && attribute.getType().equals(AttributeRep.kKey)) {
      /* Sub Entities cannot have key attributes */
            return false;
        } else if (attribute.getAttributedElement() != null) {
      /* Attribute already belongs to another element */
            return false;
        } else if (attributeAlreadyExists(attribute)) {
      /* An attribute with the same name is already connected to this element */
            return false;
        }
        return true;
    }

    /**
     * Returns if this attributed element is a sub entity of an ISA Relationship
     *
     * @return boolean to indicate if this attributed element is a sub entity
     */
    abstract protected boolean isSubEntity();

    /**
     * Returns all the attributes of this attributed element
     *
     * @return attributes of this attributed element
     */
    public ArrayList getAttributes() {
        return fAttributes;
    }

    /**
     * Returns the primary key of this attributed element
     *
     * @return primary key attributes
     */
    abstract public ArrayList getPrimaryKey();

    /**
     * Removes an attribute from the AttributedElement
     *
     * @param attribute Attribute to remove
     * @throws AttributedElementException Attribute doesn't exist
     */
    public void removeAttribute(final AttributeRep attribute) {
        fAttributes.remove(attribute);
        attribute.setAttributedElement(null);
    }

    /**
     * Returns the mapped relation of this attributed element
     *
     * @return mapped relation
     */
    abstract public Relation getMappedRelation();

    /**
     * Writes the XML rule for the attributelist of an attributed element to the write buffer
     *
     * @param out Place to write to
     * @throws IOException
     */
    protected void writeAttributeList(final Writer out) throws IOException {
        out.write("\t\t<attributelist>\n");
        Iterator itAttributes = fAttributes.iterator();
        while (itAttributes.hasNext()) {
            AttributeRep attribute = (AttributeRep) itAttributes.next();
            out.write("\t\t\t<attribute id=\"" + attribute.getID() + "\"></attribute>\n");
        }
        out.write("\t\t</attributelist>\n");
    }

    /**
     * Writes the XML rule for an attributedElement to the write buffer
     *
     * @param out Place to write to
     * @throws IOException
     */
    abstract public void write(final Writer out) throws IOException;

    /**
     * Writes the XML rule for a reference (id) to an attributed element to the write buffer
     *
     * @param out place to write to
     * @throws IOException
     */
    abstract public void writeReference(final Writer out) throws IOException;

    /**
     * Returns if an attribute with the same name is already connected to this attributed element
     *
     * @param attribute Attribute we want to test
     * @return boolean to indicate if an attribute with the same name already is connected
     */
    protected boolean attributeAlreadyExists(final AttributeRep attribute) {
        Iterator itAttributes = fAttributes.iterator();
        while (itAttributes.hasNext()) {
            AttributeRep currentAttribute = (AttributeRep) itAttributes.next();
            if ((currentAttribute != attribute) && (attribute.getName().equals(currentAttribute.getName())))
                return true;
        }
        return false;
    }

}
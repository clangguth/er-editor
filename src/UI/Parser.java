package UI;

import Exceptions.ParseException;
import Reps.*;
import Shapes.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.HashMap;

/**
 * <p>Title: Parser</p>
 * <p>Description: Parser to parse an XML File to an ER Diagram</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 13/06/2003
 */
public class Parser extends DefaultHandler {

    private HashMap fElements = new HashMap();
    private ERDiagram fDiagram;
    private String fId;

    /**
     * Constructor for the XML Parser
     *
     * @param diagram ER Diagram
     * @throws ParseException
     */
    public Parser(ERDiagram diagram) throws ParseException {
        if (diagram == null) throw new ParseException("Parsing Error: Illegal Diagram");
        fDiagram = diagram;
    }

    /**
     * Run the parser on a given file
     *
     * @param file XML File to parse
     * @throws ParseException Illegal XML File
     */
    public void run(File file) throws ParseException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(file, this);
        } catch (Exception e) {
            throw new ParseException("Parsing Error: Illegal XML File\n" + e.getMessage());
        }
    }

    /**
     * SAXParser calls startDocument() when it starts parsing a document
     *
     * @throws SAXException XML Parse Error
     */
    public void startDocument() throws SAXException {
    }

    /**
     * SAXParser calls endDocument() when it finishes parsing a document
     *
     * @throws SAXException XML Parse Error
     */
    public void endDocument() throws SAXException {
    }

    /**
     * SAXParser calls startElement() when it encounters an opening element tag (eg <item>)
     *
     * @param uri       uri
     * @param localName localName
     * @param name      XML Element Name
     * @param attrs     XML Attributes
     * @throws SAXException XML Parse Error
     */
    public void startElement(String uri, String localName, String name, Attributes attrs) throws SAXException {
        try {
            if (name.equals("entityid")) {
                addEntity(attrs);
            } else if (name.equals("relationshipid")) {
                addRelationship(attrs);
            } else if (name.equals("weakentityid")) {
                addWeakEntity(attrs);
            } else if (name.equals("weakrelationshipid")) {
                addWeakRelationship(attrs);
            } else if (name.equals("attributeid")) {
                addAttribute(attrs);
            } else if (name.equals("isaid")) {
                addISA(attrs);
            } else if (name.equals("attribute")) {
                addAttributeToElement(attrs);
            } else if (name.equals("role")) {
                addRoleToElement(attrs);
            } else if (name.equals("entity")) {
                fId = attrs.getValue("id");
            } else if (name.equals("relationship")) {
                fId = attrs.getValue("id");
            } else if (name.equals("weakentity")) {
                fId = attrs.getValue("id");
            } else if (name.equals("weakrelationship")) {
                fId = attrs.getValue("id");
            } else if (name.equals("isa")) {
                fId = attrs.getValue("id");
            } else if (name.equals("superentity")) {
                setSuperEntity(attrs);
            } else if (name.equals("subentity")) {
                addSubEntity(attrs);
            } else if (name.equals("dependencyref")) {
                addDependentElement(attrs);
            }
        } catch (ParseException e) {
            throw new SAXException(e.getMessage());
        }
    }

    /**
     * SAXParser calls endElement() when it encounters a closing element tag (eg </item>)
     *
     * @param name XML Element Name
     * @throws SAXException XML Parse Error
     */
    public void endElement(String name) throws SAXException {
    }

    /**
     * SAXParser calls characters() when it encounters text outside of any tags
     *
     * @param p0 p0
     * @param p1 p1
     * @param p2 p2
     */
    public void characters(char[] p0, int p1, int p2) {
    }

    /**
     * Add an entity to the diagram
     *
     * @param attrs XML Attributes
     */
    private void addEntity(Attributes attrs) {
        String id = attrs.getValue("id");
        int x = new Integer(attrs.getValue("posX")).intValue();
        int y = new Integer(attrs.getValue("posY")).intValue();
        String name = attrs.getValue("name");
        EntityRep entityRep = new EntityRep(name);
        Entity entity = new Entity(fDiagram, entityRep, x, y);
        entity.adjustWidthToName(fDiagram);
        fDiagram.add(entity);
        fElements.put(id, entity);
    }

    /**
     * Add a relationship to the diagram
     *
     * @param attrs XML Attributes
     */
    private void addRelationship(Attributes attrs) {
        String id = attrs.getValue("id");
        int x = new Integer(attrs.getValue("posX")).intValue();
        int y = new Integer(attrs.getValue("posY")).intValue();
        String name = attrs.getValue("name");
        RelationshipRep relationshipRep = new RelationshipRep(name);
        Relationship relationship = new Relationship(fDiagram, relationshipRep, x, y);
        relationship.adjustWidthToName(fDiagram);
        fDiagram.updateFrameSize(x, y);
        fDiagram.add(relationship);
        fElements.put(id, relationship);
    }

    /**
     * Add a weak entity to the diagram
     *
     * @param attrs XML Attributes
     */
    private void addWeakEntity(Attributes attrs) {
        String id = attrs.getValue("id");
        int x = new Integer(attrs.getValue("posX")).intValue();
        int y = new Integer(attrs.getValue("posY")).intValue();
        String name = attrs.getValue("name");
        EntityRep weakEntityRep = new EntityRep(name);
        Entity weakEntity = new Entity(fDiagram, weakEntityRep, x, y);
        weakEntity.adjustWidthToName(fDiagram);
        fDiagram.add(weakEntity);
        fElements.put(id, weakEntity);
    }

    /**
     * Add a weak relationship to the diagram
     *
     * @param attrs XML Attributes
     */
    private void addWeakRelationship(Attributes attrs) {
        String id = attrs.getValue("id");
        int x = new Integer(attrs.getValue("posX")).intValue();
        int y = new Integer(attrs.getValue("posY")).intValue();
        String name = attrs.getValue("name");
        RelationshipRep weakRelationshipRep = new RelationshipRep(name);
        Relationship weakRelationship = new Relationship(fDiagram, weakRelationshipRep, x, y);
        weakRelationship.adjustWidthToName(fDiagram);
        fDiagram.add(weakRelationship);
        fElements.put(id, weakRelationship);
    }

    /**
     * Add an attribute to the diagram
     *
     * @param attrs XML Attributes
     * @throws ParseException Illegal XML File
     */
    private void addAttribute(Attributes attrs) throws ParseException {
        String id = attrs.getValue("id");
        int x = new Integer(attrs.getValue("posX")).intValue();
        int y = new Integer(attrs.getValue("posY")).intValue();
        String type = attrs.getValue("type");
        String name = attrs.getValue("name");
        boolean required = new Boolean(attrs.getValue("required")).booleanValue();
        boolean unique = new Boolean(attrs.getValue("unique")).booleanValue();
        String datatype = attrs.getValue("datatype");
        int length = new Integer(attrs.getValue("length")).intValue();
        AttributeRep attributeRep = new AttributeRep(name, type);
        Attribute attribute = new Attribute(fDiagram, attributeRep, x, y);
        attribute.adjustWidthToName(fDiagram);
        attributeRep.setDataType(datatype);
        try {
            attributeRep.setRequired(required);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
        attributeRep.setUnique(unique);
        attributeRep.setLength(length);
        fDiagram.add(attribute);
        fElements.put(id, attribute);
    }

    /**
     * Add an ISA Relationship to the diagram
     *
     * @param attrs XML Attributes
     */
    private void addISA(Attributes attrs) {
        String id = attrs.getValue("id");
        int x = new Integer(attrs.getValue("posX")).intValue();
        int y = new Integer(attrs.getValue("posY")).intValue();
        ISARep isaRep = new ISARep();
        ISA isa = new ISA(fDiagram, isaRep, x, y);
        fDiagram.updateFrameSize(x, y);
        fDiagram.add(isa);
        fElements.put(id, isa);
    }

    /**
     * Connect an attribute to an Attributed Element
     *
     * @param attrs XML Attributes
     * @throws ParseException Illegal XML File
     */
    private void addAttributeToElement(Attributes attrs) throws ParseException {
        DrawableElement drawableElement = ((DrawableElement) fElements.get(fId));
        Attribute attribute = (Attribute) fElements.get(attrs.getValue("id"));
        try {
            drawableElement.connect(null, attribute);
            Line line = new Line(fDiagram, null, drawableElement, attribute);
            fDiagram.add(line);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     * Add a role-connection to an element
     *
     * @param attrs XML Attributes
     * @throws ParseException Illegal XML File
     */
    private void addRoleToElement(Attributes attrs) throws ParseException {
        String mincard = attrs.getValue("mincard");
        String maxcard = attrs.getValue("maxcard");
        String name = attrs.getValue("name");
        boolean refintegrity = new Boolean(attrs.getValue("refintegrity")).booleanValue();
        Entity entity = (Entity) fElements.get(attrs.getValue("entityid"));
        Relationship relationship = (Relationship) fElements.get(fId);
        try {
            Role role = entity.connect(null, relationship);
            role.setMaxCard(maxcard);
            role.setMinCard(mincard);
            role.setName(name);
            if (refintegrity) role.setRefIntegrity(refintegrity, true);
            Line line = new Line(fDiagram, role, entity, relationship);
            fDiagram.add(line);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     * Add a subentity to an ISA Relationship
     *
     * @param attrs XML Attributes
     * @throws ParseException Illegal XML File
     */
    private void addSubEntity(Attributes attrs) throws ParseException {
        Entity entity = (Entity) fElements.get(attrs.getValue("id"));
        ISA isa = (ISA) fElements.get(fId);
        try {
            isa.connect(null, entity);
            Line line = new Line(fDiagram, null, isa, entity);
            fDiagram.add(line);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     * Sets the superentity of an ISA Relationship
     *
     * @param attrs XML Attributes
     * @throws ParseException Illegal XML File
     */
    private void setSuperEntity(Attributes attrs) throws ParseException {
        Entity entity = (Entity) fElements.get(attrs.getValue("id"));
        ISA isa = (ISA) fElements.get(fId);
        try {
            entity.connect(null, isa);
            Line line = new Line(fDiagram, null, entity, isa);
            fDiagram.add(line);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     * Add a dependent entity to an entity
     *
     * @param attrs XML Attributes
     * @throws ParseException Illegal XML File
     */
    private void addDependentElement(Attributes attrs) throws ParseException {
        Entity weakEntity = (Entity) fElements.get(fId);
        Relationship relationship = (Relationship) fElements.get(attrs.getValue("relationshipid"));
        try {
            ((EntityRep) weakEntity.getRep()).addDependency((RelationshipRep) relationship.getRep(), true);
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

}
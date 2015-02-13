package Reps;

import Exceptions.EntityRepException;
import Exceptions.RelationshipRepException;
import Mapping.Relation;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: EntityRep</p>
 * <p>Description: A class representing an internal entity</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 20/06/2003
 */
public class EntityRep extends AttributedElement {

    /* Holds the number of entities made so far */
    private static int fEntityRepCount = 0;

    /* ArrayList with all the super entities of this entity (empty if there are none) */
    private ArrayList fSuperEntities = new ArrayList();

    /* ArrayList with all the relationships that have a role to this entity */
    private ArrayList fConnectedRelationships = new ArrayList();

    /* ArrayList with all the entities on which this weak entity depends */
    private ArrayList fDependentRelationships = new ArrayList();

    /**
     * Constructor 1
     *
     * @param name Name of the entity
     */
    public EntityRep(final String name) {
        super(name);
        fEntityRepCount++;
    }

    /**
     * Constructor 2 (without a name)
     */
    public EntityRep() {
        super("Entity" + (++EntityRep.fEntityRepCount));
    }

    /**
     * Returns the relationships connected to this entity
     *
     * @return list of connected relationships
     */
    public ArrayList getConnectedRelationships() {
        return fConnectedRelationships;
    }

    /**
     * Returns if this entity is (directly) dependent via a given relationship
     *
     * @param relationship Relationship to test
     * @return boolean to indicate dependency via a relationship
     */
    public boolean hasDependency(RelationshipRep relationship) {
        return fDependentRelationships.contains(relationship);
    }

    /**
     * Returns all the key attributes of this entity
     *
     * @return list of key attributes
     */
    private ArrayList getAllKeyAttributes() {
        ArrayList keyAttributes = new ArrayList();
        Iterator itAttributes = getAttributes().iterator();
        while (itAttributes.hasNext()) {
            AttributeRep attr = (AttributeRep) itAttributes.next();
            if (attr.getType().equals(AttributeRep.kKey)) keyAttributes.add(attr);
        }
    /* Key attributes of a weak entity become key attributes of this entity */
        Iterator itRelationships = fDependentRelationships.iterator();
        while (itRelationships.hasNext()) {
            RelationshipRep relationship = ((RelationshipRep) itRelationships.next());
            EntityRep entity = relationship.getOtherEntityFromTwo(this);
            keyAttributes.addAll(entity.getPrimaryKey());
        }
        return keyAttributes;
    }

    /**
     * Returns all the non-key attributes of this entity
     *
     * @return list of non-key attributes
     */
    private ArrayList getAllNonKeyAttributes() {
        ArrayList attributes = new ArrayList();
        Iterator itAttributes = getAttributes().iterator();
        while (itAttributes.hasNext()) {
            AttributeRep attr = (AttributeRep) itAttributes.next();
            if (!attr.getType().equals(AttributeRep.kKey)) attributes.add(attr);
        }
        return attributes;
    }

    /**
     * Returns if this entity contains key attributes
     *
     * @return boolean to indicate if the AttributedElement contains key attributes
     */
    public boolean hasKeyAttributes() {
        return (!getAllKeyAttributes().isEmpty());
    }

    /**
     * Returns the primary key of the entity
     *
     * @return list with the primary key attributes
     */
    public ArrayList getPrimaryKey() {
        if (fSuperEntities.isEmpty()) {
            return getAllKeyAttributes();
        } else {
            ArrayList primaryKey = new ArrayList();
            Iterator itSuperEntities = fSuperEntities.iterator();
            while (itSuperEntities.hasNext()) {
                EntityRep entity = ((EntityRep) itSuperEntities.next());
                Iterator itKeyAttributes = entity.getPrimaryKey().iterator();
                while (itKeyAttributes.hasNext()) {
                    AttributeRep attribute = ((AttributeRep) itKeyAttributes.next());
                    if (!primaryKey.contains(attribute)) primaryKey.add(attribute);
                }
            }
            return primaryKey;
        }
    }

    /**
     * Checks if this entity is valid
     *
     * @return string with the reason why this entity is not OK
     */
    public String check() {
        if (getPrimaryKey().isEmpty()) {
            return "Entity \"" + getName() + "\" has no primary key";
        }
        return null;
    }

    /**
     * Returns the mapped relation of this entity
     *
     * @return mapped relation
     */
    public Relation getMappedRelation() {
        Relation relation = new Relation(getName(), false);
        relation.addAllKeyAttributes(getPrimaryKey());
        relation.addAllNonKeyAttributes(getAllNonKeyAttributes());
        return relation;
    }

    /**
     * Removes a superentity of this entity
     *
     * @param entity Super Entity to remove
     */
    protected void removeSuperEntity(final EntityRep entity) {
        fSuperEntities.remove(entity);
    }

    /**
     * Returns if this entity is weak
     *
     * @return boolean to indicate if this entity is weak
     */
    public boolean isWeak() {
        return !fDependentRelationships.isEmpty();
    }

    /**
     * Add a dependent relationship
     *
     * @param relationship Depentent relationship to add
     * @param skipCheck    should be false, only the parser should use 'true' here !!!!
     * @throws EntityRepException Sub Entity cannot have key attributes, so it cannot be weak
     * @throws EntityRepException Relationship is not connected to this entity
     * @throws EntityRepException Entity already depends on this relationship
     */
    public void addDependency(final RelationshipRep relationship, final boolean skipCheck) throws EntityRepException {
        try {
            if (!skipCheck) {
                if (fSuperEntities.size() != 0) {
                    throw new EntityRepException("Sub Entity cannot have key attributes, so it cannot be weak...");
                } else if (!fConnectedRelationships.contains(relationship)) {
                    throw new EntityRepException("Relationship is not connected to this entity...");
                } else if (fDependentRelationships.contains(relationship)) {
                    throw new EntityRepException("Entity already depends on this relationship...");
                } else if (relationship.getRoles().size() == 2 && isDependentOn(relationship.getOtherEntityFromTwo(this))) {
                    throw new EntityRepException("Entity already depends on this relationship...");
                }
            }
            relationship.setWeakEntity(this, skipCheck);
            fDependentRelationships.add(relationship);
        } catch (RelationshipRepException e) {
            throw new EntityRepException(e.getMessage());
        }
    }

    /**
     * Returns if a relationship can be set as dependency for this entity
     *
     * @param relationship Relationship of which we want to know if it can be set as dependent
     * @return boolean to indicate if the relationship can be set dependent
     */
    public boolean canBeSetDependentOf(final RelationshipRep relationship) {
        if (fSuperEntities.size() != 0) {
      /* Sub Entity cannot have key attributes, so it cannot be weak */
            return false;
        } else if (!fConnectedRelationships.contains(relationship)) {
      /* Relationship is not connected to this entity */
            return false;
        } else if (fDependentRelationships.contains(relationship)) {
      /* Entity already depends on this relationship */
            return false;
        } else if (relationship.getRoles().size() == 2 && isDependentOn(relationship.getOtherEntityFromTwo(this))) {
      /* Entity already depends on this relationship */
            return false;
        }
        return relationship.canBeSetWeak(this);
    }

    /**
     * Removes a dependent relationship
     *
     * @param relationship Dependent relationship to remove
     */
    public void removeDependency(final RelationshipRep relationship) {
        relationship.removeWeakEntity();
        fDependentRelationships.remove(relationship);
    }

    /**
     * Removes an attribute from this entity
     *
     * @param attribute Attribute to remove
     */
    public void removeAttribute(final AttributeRep attribute) {
        super.removeAttribute(attribute);
        adjustDependencies();
    }

    /**
     * Returns all the relationships dependent for this entity
     *
     * @return list of dependent relationships
     */
    public ArrayList getDependentRelationships() {
        return fDependentRelationships;
    }

    /**
     * Writes the XML rule for a reference to this entity to the write buffer
     *
     * @param out place to write to
     * @throws IOException
     */
    public void writeReference(final Writer out) throws IOException {
        if (isWeak()) {
            out.write("\t<weakentity id=\"" + getID() + "\">\n");
            writeAttributeList(out);
            out.write("\t\t<dependencylist>\n");
            Iterator itDependentElements = fDependentRelationships.iterator();
            while (itDependentElements.hasNext()) {
                RelationshipRep relationship = ((RelationshipRep) itDependentElements.next());
                out.write("\t\t\t<dependencyref relationshipid=\"" + relationship.getID() + "\"></dependencyref>\n");
            }
            out.write("\t\t</dependencylist>\n");
            out.write("\t</weakentity>\n");
        } else if (!getAttributes().isEmpty()) {
            out.write("\t<entity id=\"" + getID() + "\">\n");
            writeAttributeList(out);
            out.write("\t</entity>\n");
        }
    }

    /**
     * Writes the XML rule for this entity to the write buffer
     *
     * @param out place to write to
     * @throws IOException
     */
    public void write(final Writer out) throws IOException {
        if (isWeak()) {
            out.write("\t\t<weakentityid id=\"" + getID() + "\" posX=\"" + getX() + "\" posY=\"" + getY() + "\" name=\"" + getName() + "\">");
            out.write("</weakentityid>\n");
        } else {
            out.write("\t\t<entityid id=\"" + getID() + "\" posX=\"" + getX() + "\" posY=\"" + getY() + "\" name=\"" + getName() + "\">");
            out.write("</entityid>\n");
        }
    }

    /**
     * Returns if this entity gives dependency to some other entity/relationship
     *
     * @return boolean to indicate dependency-giving of this entity
     */
    protected boolean givesDependency() {
        Iterator itRelationships = fConnectedRelationships.iterator();
        while (itRelationships.hasNext()) {
            RelationshipRep relationship = ((RelationshipRep) itRelationships.next());
            if (relationship.isWeak() && !relationship.getWeakEntity().equals(this)) return true;
        }
        return false;
    }

    /**
     * Update dependencies between this entity and other enitities/relationships
     */
    private void adjustDependencies() {
        if (getPrimaryKey().isEmpty()) {
            Iterator itRelationships = fConnectedRelationships.iterator();
            while (itRelationships.hasNext()) {
                RelationshipRep relationship = ((RelationshipRep) itRelationships.next());
                if (relationship.isWeak()) {
                    EntityRep weakEntity = relationship.getWeakEntity();
                    weakEntity.removeDependency(relationship);
                    weakEntity.adjustDependencies();
                }
            }
        }
    }

    /**
     * Adds a superentity
     *
     * @param entity Super Entity to add
     */
    protected void addSuperEntity(final EntityRep entity) {
    /* Check necessary for multiple inheritance */
        if (!fSuperEntities.contains(entity)) {
            fSuperEntities.add(entity);
        }
    }

    /**
     * Adds a connected relationship to this entity
     *
     * @param relationship Relationship to add
     */
    protected void addConnectedRelationship(final RelationshipRep relationship) {
        fConnectedRelationships.add(relationship);
    }

    /**
     * Removes a connected relationship from this entity
     *
     * @param relationship Relationship to remove
     */
    protected void removeConnectedRelationship(final RelationshipRep relationship) {
        fConnectedRelationships.remove(relationship);
    }

    /**
     * Returns if an entity is a (direct or indirect) super entity of this entity
     * Internal recursive function used to check for ISA cycles
     *
     * @param superEntity Entity to check
     * @return boolean to indicate if the entity is a super entity of this entity
     */
    protected boolean isSuperEntityOf(final EntityRep superEntity) {
        Iterator itSuperEntities = fSuperEntities.iterator();
        while (itSuperEntities.hasNext()) {
            EntityRep entity = ((EntityRep) itSuperEntities.next());
            if (entity.equals(superEntity)) return true;
            else return entity.isSuperEntityOf(superEntity);
        }
        return false;
    }

    /**
     * Returns if this entity depends on a given entity
     *
     * @param entity Entity to test
     * @return boolean to indicate dependency of this entity on a given entity
     */
    private boolean isDependentOn(final EntityRep entity) {
        ArrayList key = entity.getPrimaryKey();
        Iterator itKeyAttributes = key.iterator();
        while (itKeyAttributes.hasNext()) {
            AttributeRep keyAttribute = ((AttributeRep) itKeyAttributes.next());
            if (keyAttribute.getAttributedElement().equals(this)) return true;
        }
        return false;
    }

    /**
     * Returns if this entity is a sub entity
     *
     * @return boolean to indicate if this entity is a subentity or not
     */
    protected boolean isSubEntity() {
        return (fSuperEntities.size() != 0);
    }

}
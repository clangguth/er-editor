package Reps;

import Exceptions.RelationshipRepException;
import Exceptions.RoleException;
import Mapping.Relation;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: RelationshipRep</p>
 * <p>Description: A class representing an internal relationship</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 23/06/2003
 */
public class RelationshipRep extends AttributedElement {

    /* Container with all the roles of this AttributedElement */
    private ArrayList fRoles = new ArrayList();

    /* Holds the number of relationships made so far */
    private static int fRelationshipRepCount = 0;

    /* Weak entity connected to this (weak) relationship */
    private EntityRep fWeakEntity = null;

    /**
     * Constructor 1
     *
     * @param name name of the relationship
     */
    public RelationshipRep(final String name) {
        super(name);
        fRelationshipRepCount++;
    }

    /**
     * Constructor 2 (without a name)
     */
    public RelationshipRep() {
        super("Rel" + (++fRelationshipRepCount));
    }

    /**
     * Adds a role to this relationshiop
     *
     * @param role Role to add
     * @throws RoleException Only 2 roles to the same entity are allowed
     * @throws RoleException Only 2 roles are allowed for a weak relationship
     */
    public void addRole(final Role role) throws RoleException {
        if (isWeak() && fRoles.size() == 2) {
            throw new RoleException("Only 2 roles are allowed for a weak relationship...");
        }
        EntityRep entity = role.getEntity();
        if (isRecursive(entity)) {
            throw new RoleException("Only 2 roles to the same entity are allowed...");
        }
        fRoles.add(role);
        entity.addConnectedRelationship(this);
    }

    /**
     * Returns if a role can be added to this Relationship
     *
     * @param role Role of which we want to know if it can be added
     * @return boolean to indicate if the role can be added
     */
    public boolean canAddRole(final Role role) {
        if (isWeak() && fRoles.size() == 2) {
      /* Only 2 roles are allowed for a weak relationship */
            return false;
        } else if (isRecursive(role.getEntity())) {
      /* Only 2 roles to the same entity are allowed */
            return false;
        }
        return true;
    }

    /**
     * Removes a role from this relationship
     *
     * @param role role to remove
     * @throws RoleException Attribute doesn't exist
     */
    public void removeRole(final Role role) throws RoleException {
        if (!fRoles.contains(role)) {
            throw new RoleException("Role doesn't exist...");
        }
        EntityRep entity = role.getEntity();
        entity.removeConnectedRelationship(this);
    /* inform weak entity if necessary */
        if (isWeak()) fWeakEntity.removeDependency(this);
        fRoles.remove(role);
    }

    /**
     * Returns if this relationship is weak
     *
     * @return boolean to indicate if this relationship is weak
     */
    public boolean isWeak() {
        return fWeakEntity != null;
    }

    /**
     * Returns if this relationship is recursive
     *
     * @return boolean to indicate if this relationship is recursive
     */
    public boolean isRecursive(EntityRep entity) {
        int count = 0;
        Iterator itRoles = fRoles.iterator();
        while (itRoles.hasNext()) {
            Role role = ((Role) itRoles.next());
            EntityRep entity1 = role.getEntity();
            if (entity1.equals(entity)) count++;
        }
        return count > 1;
    }

    /**
     * Returns the first recursive role
     *
     * @param entity Entity to test
     * @return boolean
     */
    public Role getFirstRecursiveRole(final EntityRep entity) {
        int count = 0;
        Iterator itRoles = fRoles.iterator();
        while (itRoles.hasNext()) {
            Role role = ((Role) itRoles.next());
            if (role.getEntity().equals(entity)) return role;
        }
        return null;
    }

    /**
     * Returns the weak entity of this (weak) relationship
     *
     * @return weak entity connected to this (weak) relationship
     */
    public EntityRep getWeakEntity() {
        return fWeakEntity;
    }

    /**
     * Sets the weak entity connected to this weak relationship
     *
     * @param entity    Entity to connect to this relationship
     * @param skipCheck should be false, only the parser should use 'true' here !!!!
     * @throws RelationshipRepException
     */
    protected void setWeakEntity(final EntityRep entity, final boolean skipCheck) throws RelationshipRepException {
        if (!skipCheck) {
            if (fRoles.size() != 2) {
                throw new RelationshipRepException("Weak relationship should have exactly two roles...");
            } else if (isWeak()) {
                throw new RelationshipRepException("Relationship is already weak...");
            } else if (getOtherEntityFromTwo(entity).getPrimaryKey().isEmpty()) {
                throw new RelationshipRepException("Entity should have a primary key...");
            }
        }
        fWeakEntity = entity;
    }

    /**
     * Returns always false, because this cannot be a sub entity
     *
     * @return false
     */
    protected boolean isSubEntity() {
        return false;
    }

    /**
     * Returns if a (weak) entity can be connected to this weak relationship
     *
     * @param entity Entity to test
     * @return boolean to indicate if it can be connected
     */
    public boolean canBeSetWeak(final EntityRep entity) {
        if (fRoles.size() != 2) {
      /* Weak relationship should have exactly two roles */
            return false;
        } else if (isWeak()) {
      /* Relationship is already weak */
            return false;
        } else if (getOtherEntityFromTwo(entity).getPrimaryKey().isEmpty()) {
      /* Entity should have a primary key */
            return false;
        }
        return true;
    }

    /**
     * Removes the weak entity, connected to this relationship
     */
    protected void removeWeakEntity() {
        fWeakEntity = null;
    }

    /**
     * Returns the other entity if this relationship has two roles
     *
     * @param entity Entity we do 'not' want
     * @return Other entity connected to this relationship
     */
    protected EntityRep getOtherEntityFromTwo(final EntityRep entity) {
        if (fRoles.size() == 2) {
            EntityRep entity1 = ((Role) fRoles.get(0)).getEntity();
            EntityRep entity2 = ((Role) fRoles.get(1)).getEntity();
            if (entity1.equals(entity)) return entity2;
            else return entity1;
        }
        return null;
    }

    /**
     * Checks if this relationship is valid
     *
     * @return string with the reason why this relationship is not OK
     */
    public String check() {
        if (fRoles.size() <= 1) {
            return "Relationship \"" + getName() + "\" should have at least 2 entities";
        }
        return null;
    }

    /**
     * Returns the primary key of the relationship
     *
     * @return container with the primary key attributes
     */
    public ArrayList getPrimaryKey() {
        ArrayList primaryKey = new ArrayList();
        Iterator itRoles = fRoles.iterator();
        while (itRoles.hasNext()) {
            Role role = ((Role) itRoles.next());
            primaryKey.addAll(role.getEntity().getPrimaryKey());
        }
        return primaryKey;
    }

    /**
     * Returns the mapped relation of this relationship
     *
     * @return mapped relation
     */
    public Relation getMappedRelation() {
        if (isWeak()) return null;
        Relation relation = new Relation(getName(), true);
        if (fRoles.size() == 2) {
            Role role1 = (Role) fRoles.get(0);
            Role role2 = (Role) fRoles.get(1);
            if (role1.isOneOne()) {
                relation = role2.getEntity().getMappedRelation();
                relation.addAllNonKeyAttributes(role1.getEntity().getPrimaryKey());
                return relation;
            } else if (role2.isOneOne()) {
                relation = role1.getEntity().getMappedRelation();
                relation.addAllNonKeyAttributes(role2.getEntity().getPrimaryKey());
                return relation;
            }
        }
        relation.addAllNonKeyAttributes(getAttributes());
        relation.addAllKeyAttributes(getPrimaryKey());
        return relation;
    }

    /**
     * Returns the roles of the AttributedElement
     *
     * @return roles of the AttributedElement
     * @see #addRole
     * @see #getRoles
     */
    public ArrayList getRoles() {
        return fRoles;
    }

    /**
     * Writes the XML rule for a reference (id) of this relationship to the write buffer
     *
     * @param out place to write to
     * @throws IOException
     */
    public void writeReference(final Writer out) throws IOException {
        if (isWeak()) {
            out.write("\t<weakrelationship id=\"" + getID() + "\">\n");
        } else {
            out.write("\t<relationship id=\"" + getID() + "\">\n");
            if (!getAttributes().isEmpty()) {
                writeAttributeList(out);
            }
        }
        if (!fRoles.isEmpty()) {
            out.write("\t\t<rolelist>\n");
            Iterator itRole = fRoles.iterator();
            while (itRole.hasNext()) {
                Role role = ((Role) itRole.next());
                role.write(out);
            }
            out.write("\t\t</rolelist>\n");
        }
        if (isWeak()) {
            out.write("\t</weakrelationship>\n");
        } else {
            out.write("\t</relationship>\n");
        }
    }

    /**
     * Writes the XML rule for this relationship to the write buffer
     *
     * @param out place to write to
     * @throws IOException
     */
    public void write(final Writer out) throws IOException {
        if (isWeak()) {
            out.write("\t\t<weakrelationshipid id=\"" + getID() + "\" posX=\"" + getX() + "\" posY=\"" + getY() + "\" name=\"" + getName() + "\">");
            out.write("</weakrelationshipid>\n");
        } else {
            out.write("\t\t<relationshipid id=\"" + getID() + "\" posX=\"" + getX() + "\" posY=\"" + getY() + "\" name=\"" + getName() + "\">");
            out.write("</relationshipid>\n");
        }
    }
}
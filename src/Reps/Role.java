package Reps;

import Exceptions.RoleException;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>Title: Role</p>
 * <p>Description: A class representing an internal role</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 20/06/2003
 */
public class Role extends Element {

    public static final int kNormal = 0;
    public static final int kRecursiveA = 90;
    public static final int kRecursiveB = -90;
    public static final int kMaxCard = Integer.MAX_VALUE;

    /* Minimal Cardinality */
    private int fMinCard = 1;

    /* Maximal Cardinality */
    private int fMaxCard = kMaxCard;

    /* Boolean that states if referential integrity is on or not */
    private boolean fRefIntegrity = false;

    /* Entity of the role */
    private EntityRep fEntity = null;

    /* Relationship of the role */
    private RelationshipRep fRelationship = null;

    /**
     * @param entity       Entity of the role
     * @param relationship Relationship of the role
     *                     Constructor
     */
    public Role(EntityRep entity, RelationshipRep relationship) {
        super("");
        fEntity = entity;
        fRelationship = relationship;
    }

    /**
     * Sets the minimal cardinality of the role
     *
     * @param minCardStr Minimal cardinality to set
     * @throws RoleException Turn off referential integrity first
     * @throws RoleException Maximum cardinality should be greater or equal to the minimum cardinality
     */
    public void setMinCard(final String minCardStr) throws RoleException {
        int minCard;
        if (minCardStr.equals("M") || minCardStr.equals("m")) {
            minCard = kMaxCard;
        } else {
            minCard = new Integer(minCardStr).intValue();
        }
        if (fRefIntegrity) {
            throw new RoleException("Turn off referential integrity first...");
        } else if (minCard > fMaxCard) {
            throw new RoleException("Maximum cardinality should be greater or equal to the minimum cardinality...");
        }
        fMinCard = minCard;
    }

    /**
     * Sets the maximal cardinality of the role
     *
     * @param maxCardStr Maximal cardinality to set
     * @throws RoleException Turn off referential integrity first
     * @throws RoleException Maximum cardinality should be greater or equal to the minimum cardinality
     */
    public void setMaxCard(final String maxCardStr) throws RoleException {
        int maxCard;
        if (maxCardStr.equals("N") || maxCardStr.equals("n")) {
            maxCard = kMaxCard;
        } else {
            maxCard = new Integer(maxCardStr).intValue();
        }
        if (fRefIntegrity) {
            throw new RoleException("Turn off referential integrity first...");
        } else if (fMinCard > maxCard) {
            throw new RoleException("Maximum cardinality should be greater or equal to the minimum cardinality...");
        }
        fMaxCard = maxCard;
    }

    /**
     * Set Referential Integrity on or off
     *
     * @param refIntegrity Boolean to set ref integrity on or off
     * @param skipCheck    should be false, only the parser should use 'true' here !!!!
     * @throws RoleException can only be set in a 1-1 relationship
     */
    public void setRefIntegrity(final boolean refIntegrity, final boolean skipCheck) throws RoleException {
        if (!skipCheck) {
            if (refIntegrity && ((fMinCard != 1) || (fMaxCard != 1))) {
                throw new RoleException("This is not an 1-1 relationship...");
            }
        }
        fRefIntegrity = refIntegrity;
    }

    /**
     * Returns the minimal cardinality
     *
     * @return minimal cardinality of the role
     */
    public String getMinCard() {
        if (fMinCard == this.kMaxCard) {
            return "M";
        } else {
            return new Integer(fMinCard).toString();
        }
    }

    /**
     * Returns the maximal cardinality
     *
     * @return maximal cardinality of the role
     */
    public String getMaxCard() {
        if (fMaxCard == this.kMaxCard) {
            return "N";
        } else {
            return new Integer(fMaxCard).toString();
        }
    }

    /**
     * Returns the cardinality of the role
     *
     * @return cardinality
     */
    public String getCardinality() {
        return getMinCard() + ".." + getMaxCard();
    }

    /**
     * Returns if referential integrity is on or not
     *
     * @return boolean
     */
    public boolean getRefIntegrity() {
        return fRefIntegrity;
    }

    /**
     * Checks if this role is a one-to-one role
     *
     * @return true is this is a one-to-one role
     */
    public boolean isOneOne() {
        return (fMinCard == 1 && fMaxCard == 1);
    }

    /**
     * Returns if this role is weak
     *
     * @return boolean to indicate if the role is weak
     */
    public boolean isWeak() {
        EntityRep entity = fRelationship.getWeakEntity();
        if (entity == null) return false;
        else return (entity.equals(fEntity));
    }

    /**
     * Returns the entity of the role
     *
     * @return entity of the role
     */
    public EntityRep getEntity() {
        return fEntity;
    }

    /**
     * Returns the relationship of the role
     *
     * @return relationship of the role
     */
    public RelationshipRep getRelationship() {
        return fRelationship;
    }

    /**
     * Returns the type of recursion (A, B or normal)
     *
     * @return recursion type
     */
    public int getRecursiveType() {
        if (!fRelationship.isRecursive(fEntity)) {
            return kNormal;
        } else if (fRelationship.getFirstRecursiveRole(fEntity).equals(this)) {
            return kRecursiveA;
        } else {
            return kRecursiveB;
        }
    }

    /**
     * Checks if this role is valid
     *
     * @return string with the reason why this role is not OK
     */
    public String check() {
        return null;
    }

    /**
     * Writes the XML rule for this role to the write buffer
     *
     * @param out place to write to
     * @throws IOException
     */
    public void write(Writer out) throws IOException {
        out.write("\t\t\t<role mincard=\"" + getMinCard() + "\" maxcard=\"" + getMaxCard() + "\" name=\"" + getName() + "\" entityid=\"" + getEntity().getID() + "\" refintegrity=\"" + fRefIntegrity + "\">");
        out.write("</role>\n");
    }

    /**
     * Writes the XML rule for this role to the write buffer
     *
     * @param out place to write to
     * @throws IOException
     */
    public void writeReference(Writer out) throws IOException {
    }

}
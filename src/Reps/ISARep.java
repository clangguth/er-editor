package Reps;

import Exceptions.ISARepException;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: ISARep</p>
 * <p>Description: A class representing an internal ISA relationship</p>
 * <p>Jaarproject Programmeren 2002-2003</p>
 * <p>Company: UIA</p>
 *
 * @author Helsen Gert (s985177)
 * @version 19/06/2003
 */
public class ISARep extends Element {

    /* Super entity of this ISA Relationship */
    private EntityRep fSuperEntity = null;

    /* ArrayList with sub entities of this ISA relationship (empty if there are none) */
    private ArrayList fSubEntities = new ArrayList();

    /**
     * Constructor
     */
    public ISARep() {
        super("ISA");
    }

    /**
     * Add a subentity to the ISA Relationship
     *
     * @param entity Sub Entity of the ISA Relationship
     * @throws ISARepException Entity is the super entity of this ISA Relationship
     * @throws ISARepException Sub Entities cannot have key attributes
     * @throws ISARepException Entity is already a sub entity of the ISA Relationship
     * @throws ISARepException ISA cycles are not allowed
     */
    public void addSubEntity(final EntityRep entity) throws ISARepException {
        if (fSuperEntity == entity) {
            throw new ISARepException("Entity is the super entity of this ISA Relationship...");
        } else if (entity.hasKeyAttributes()) {
            throw new ISARepException("Sub Entities cannot have key attributes...");
        } else if (isSubEntity(entity)) {
            throw new ISARepException("Entity is already a sub entity of this ISA Relationship...");
        } else if (fSuperEntity != null && fSuperEntity.isSuperEntityOf(entity)) {
            throw new ISARepException("ISA cycles are not allowed...");
        }
        fSubEntities.add(entity);
    /* If the super entity is already known, report it to the sub entity */
        if (fSuperEntity != null) entity.addSuperEntity(fSuperEntity);
    }

    /**
     * Returns if a sub entity can be added to this ISA Relationship
     *
     * @param entity Entity of which we want to know if it can be added as sub entity
     * @return boolean to indicate if the entity can be added as sub entity
     */
    public boolean canAddSubEntity(final EntityRep entity) {
        if (fSuperEntity == entity) {
      /* Entity is the super entity of this ISA Relationship */
            return false;
        } else if (entity.hasKeyAttributes()) {
      /* Sub Entities cannot have key attributes */
            return false;
        } else if (isSubEntity(entity)) {
      /* Entity is already a sub entity of this ISA Relationship */
            return false;
        } else if (fSuperEntity != null && fSuperEntity.isSuperEntityOf(entity)) {
      /* ISA cycles are not allowed */
            return false;
        }
        return true;
    }

    /**
     * Returns the sub entities of the ISA Relationship
     *
     * @return sub entities of the ISA Relationship
     */
    public ArrayList getSubEntities() {
        return fSubEntities;
    }

    /**
     * Checks if this ISA Relationship is valid
     *
     * @return string with the reason why this ISA Relationship is not OK
     */
    public String check() {
        if (fSubEntities.isEmpty() && fSuperEntity == null) {
            return "ISA Relationship has no super entity and no sub entities";
        } else if (fSubEntities.isEmpty()) {
            return "ISA Relationship has no subentities";
        } else if (fSuperEntity == null) {
            return "ISA Relationship has no super entity";
        }
        return null;
    }

    /**
     * Returns the super entity of the ISA Relationship
     *
     * @return super entity of the ISA Relationship
     */
    public EntityRep getSuperEntity() {
        return fSuperEntity;
    }

    /**
     * Returns always false because an ISA Relationship cannot be weak
     *
     * @return false
     */
    public boolean isWeak() {
        return false;
    }

    /**
     * Set the super entity of the ISA Relationship
     *
     * @param entity Super Entity of the ISA Relationship
     * @throws ISARepException Entity is already the super entity of this ISA Relationship
     * @throws ISARepException ISA Relationship already has a super entity
     * @throws ISARepException ISA cycles are not allowed
     */
    public void setSuperEntity(final EntityRep entity) throws ISARepException {
        if (fSuperEntity == entity) {
            throw new ISARepException("Entity is already the super entity of this ISA Relationship...");
        } else if (fSuperEntity != null) {
            throw new ISARepException("ISA Relationship already has a super entity...");
        } else if (isSubEntity(entity)) {
            throw new ISARepException("ISA cycles are not allowed...");
        }
        fSuperEntity = entity;
    /* Report the super entity to the sub entities */
        Iterator itSubEntities = fSubEntities.iterator();
        while (itSubEntities.hasNext()) {
            EntityRep subEntity = ((EntityRep) itSubEntities.next());
            subEntity.addSuperEntity(entity);
        }
    }

    /**
     * Returns if an entity can be set as super entity of this ISA Relationship
     *
     * @param entity Entity of which we want to know if it can be set as super entity
     * @return boolean to indicate if the entity can be set as super entity
     */
    public boolean canSetSuperEntity(final EntityRep entity) {
        if (fSuperEntity == entity) {
      /* Entity is already the super entity of this ISA Relationship */
            return false;
        } else if (fSuperEntity != null) {
      /* ISA Relationship already has a super entity */
            return false;
        } else if (isSubEntity(entity)) {
      /* ISA cycles are not allowed */
            return false;
        }
        return true;
    }

    /**
     * Removes an entity (super or sub entity) from this ISA Relationship
     *
     * @param entity Entity to remove
     * @throws ISARepException Entity not found
     */
    public void removeEntity(final EntityRep entity) throws ISARepException {
        if (!fSubEntities.contains(entity) && (fSuperEntity != entity)) {
            throw new ISARepException("Entity not found...");
        }
        if (fSuperEntity == entity) {
      /* Removing the super entity */
            fSuperEntity = null;
      /* Report the remove of the super entity to the sub entities */
            Iterator itSubEntities = fSubEntities.iterator();
            while (itSubEntities.hasNext()) {
                EntityRep subEntity = ((EntityRep) itSubEntities.next());
                subEntity.removeSuperEntity(entity);
            }
        } else {
            fSubEntities.remove(entity);
      /* Entity is no longer a sub entity of this ISA Relationship => remove super entity from this entity */
            if (fSuperEntity != null) entity.removeSuperEntity(fSuperEntity);
        }
    }

    /**
     * Writes the XML rule for this ISA Relationship to the write buffer
     *
     * @param out Place to write to (for instance of file)
     * @throws IOException
     */
    public void write(Writer out) throws IOException {
        out.write("\t\t<isaid id=\"" + getID() + "\" posX=\"" + getX() + "\" posY=\"" + getY() + "\"></isaid>\n");
    }

    /**
     * Writes the XML rule for a reference to this ISA Relationship to the write buffer
     *
     * @param out Place to write to (for instance of file)
     * @throws IOException
     */
    public void writeReference(Writer out) throws IOException {
        out.write("\t<isa id=\"" + getID() + "\">\n");
        if (fSuperEntity != null) {
            out.write("\t\t<superentity id=\"" + fSuperEntity.getID() + "\"></superentity>\n");
        }
        if (!fSubEntities.isEmpty()) {
            out.write("\t\t<subentitylist>\n");
            Iterator itSubEntities = fSubEntities.iterator();
            while (itSubEntities.hasNext()) {
                EntityRep subEntity = ((EntityRep) itSubEntities.next());
                out.write("\t\t\t<subentity id=\"" + subEntity.getID() + "\"></subentity>\n");
            }
            out.write("\t\t</subentitylist>\n");
        }
        out.write("\t</isa>\n");
    }

    /**
     * Returns if an entity is a (direct or indirect) sub entity of this ISA Relationship
     * Internal recursive function used to check for ISA cycles
     *
     * @param entity Entity to check
     * @return boolean to indicate if the entity is a sub entity of this ISA Relationship
     */
    private boolean isSubEntity(EntityRep entity) {
        Iterator itSubEntities = fSubEntities.iterator();
        while (itSubEntities.hasNext()) {
            EntityRep subEntity = ((EntityRep) itSubEntities.next());
            if (subEntity.equals(entity)) return true;
            else return entity.isSuperEntityOf(subEntity);
        }
        return false;
    }

}
package uk.me.gumbley.minimiser.openlist;

import java.util.HashMap;
import java.util.Map;

/**
 * A bean that gives a database a name, optionally a path, and (in subclasses)
 * references to the persistence/domain layer.
 * 
 * Note that only name is used in the equality and hashCode methods. We key
 * off the name elsewhere for uniqueness (from a UI perspective, it might
 * encourage users to give their DBs unique, meaningful names... yeah, right!)
 * 
 * @author matt
 *
 */
public final class DatabaseDescriptor {
    private final String dbName;
    private Map<AttributeIdentifier, Object> attributeMap;
    
    /**
     * The attribute identifiers.
     * 
     * @author matt
     *
     */
    public enum AttributeIdentifier {
        /**
         * The database path
         */
        Path,
        
        /**
         * The database facade
         */
        Database,
        
        /**
         * The tabbed pane of views
         */
        TabbedPane,
    }
    
    /**
     * Create a new DatabaseDesriptor, given just a name, used primarily in
     * tests.
     * @param databaseName the database name
     */
    public DatabaseDescriptor(final String databaseName) {
        this(databaseName, "");
    }

    /**
     * Create a new DatabaseDesriptor, given just a name and its path.
     * @param databaseName the database name
     * @param databaseFullPath the full path to the database.
     */
    public DatabaseDescriptor(final String databaseName, final String databaseFullPath) {
        attributeMap = new HashMap<AttributeIdentifier, Object>();
        this.dbName = databaseName;
        setAttribute(AttributeIdentifier.Path, databaseFullPath == null ? "" : databaseFullPath);
    }

    /**
     * Obtain the database name
     * @return the database name
     */
    public String getDatabaseName() {
        return dbName;
    }

    /**
     * Obtain the database path.
     * @return the database path, which may be an empty string, but never null. 
     */
    public String getDatabasePath() {
        return (String) getAttribute(AttributeIdentifier.Path);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return dbName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!DatabaseDescriptor.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final DatabaseDescriptor other = (DatabaseDescriptor) obj;
        if (dbName == null) {
            if (other.dbName != null) {
                return false;
            }
        } else if (!dbName.equals(other.dbName)) {
            return false;
        }
        return true;
    }

    /**
     * Obtain the object set against a given attribute id.
     * @param attrId the attribute id
     * @return the object, or null if nothing ahs been set
     */
    public Object getAttribute(final AttributeIdentifier attrId) {
        return attributeMap.get(attrId);
    }

    /**
     * Set an object as a value against an attribute id
     * @param attrId the attribute id
     * @param object the object to set, can be null to clear, but you can
     * use clearAttribute for that, especially for Path, as that'll be set
     * to an empty string upon clearing.
     */
    public void setAttribute(final AttributeIdentifier attrId, final Object object) {
        attributeMap.put(attrId, object);
    }

    /**
     * Clear an attribute given an attribute id
     * @param attrId the attribute id
     */
    public void clearAttribute(final AttributeIdentifier attrId) {
        if (attrId == AttributeIdentifier.Path) {
            attributeMap.put(AttributeIdentifier.Path, "");
        } else {
            attributeMap.remove(attrId);
        }
    }
}

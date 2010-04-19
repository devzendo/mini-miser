/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.openlist;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JTabbedPane;

import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.util.InstanceSet;


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
    private final String mDatabaseName;
    private final String mDatabasePath;
    private JTabbedPane mTabbedPane;
    private final Map<AttributeIdentifier, Object> mAttributeMap;
    private final InstanceSet<DAOFactory> mDAOFactories;

    /**
     * The attribute identifiers.
     *
     * @author matt
     *
     */
    public enum AttributeIdentifier {
        /**
         * The application's menu structure
         */
        ApplicationMenu,
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
        mAttributeMap = new HashMap<AttributeIdentifier, Object>();
        mDAOFactories = new InstanceSet<DAOFactory>();
        this.mDatabaseName = databaseName;
        this.mDatabasePath = databaseFullPath == null ? "" : databaseFullPath;
    }

    /**
     * Obtain the database name
     * @return the database name
     */
    public String getDatabaseName() {
        return mDatabaseName;
    }

    /**
     * Obtain the database path.
     * @return the database path, which may be an empty string, but never null.
     */
    public String getDatabasePath() {
        return mDatabasePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return mDatabaseName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mDatabaseName == null) ? 0 : mDatabaseName.hashCode());
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
        if (mDatabaseName == null) {
            if (other.mDatabaseName != null) {
                return false;
            }
        } else if (!mDatabaseName.equals(other.mDatabaseName)) {
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
        return mAttributeMap.get(attrId);
    }

    /**
     * Set an object as a value against an attribute id
     * @param attrId the attribute id
     * @param object the object to set, can be null to clear, but you can
     * use clearAttribute for that, especially for Path, as that'll be set
     * to an empty string upon clearing.
     */
    public void setAttribute(final AttributeIdentifier attrId, final Object object) {
        mAttributeMap.put(attrId, object);
    }

    /**
     * Clear an attribute given an attribute id
     * @param attrId the attribute id
     */
    public void clearAttribute(final AttributeIdentifier attrId) {
        mAttributeMap.remove(attrId);
    }

    /**
     * Obtain a specific DAOFactory for a given DAOFactory subtype.
     * @param <D> a subtype of DAOFactory
     * @param daoFactoryInterface the subtype of DAOFactory
     * @return an instance of D, or null if the DAOFactory for this
     * interface has not been set.
     */
    public <D extends DAOFactory> D getDAOFactory(final Class<D> daoFactoryInterface) {
        return mDAOFactories.getInstanceOf(daoFactoryInterface);
    }

    /**
     * Store a specific DAOFactory for a given DAOFactory subtype.
     * @param <D> a subtype of DAOFactory
     * @param daoFactoryInstance the instance of the DAOFactory
     * subtype
     * @param daoFactoryInterface the subtype of DAOFactory
     */
    public <D extends DAOFactory> void setDAOFactory(
            final Class<D> daoFactoryInterface,
            final D daoFactoryInstance) {
        mDAOFactories.addInstance(daoFactoryInterface, daoFactoryInstance);
    }
    
    /**
     * Obtain the tabbed pane of view Tab objects for the database.
     * 
     * @return the tabbed pane of views
     */
    public JTabbedPane getTabbedPane() {
        return mTabbedPane;
    }
    
    /**
     * Set the tabbed pane of view Tab objects for the database.
     * 
     * @param tabbedPane the tabbed pane to set for the database 
     */
    public void setTabbedPane(final JTabbedPane tabbedPane) {
        mTabbedPane = tabbedPane;
    }
}

package org.devzendo.minimiser.plugin.facade.newdatabase;

/**
 * Plugins that want to create database content after the File|New
 * wizard has collected user input must implement this and provide
 * the facade.
 * <p>
 * It is highly likely that such plugins would want to implement
 * DatabaseOpening, as if they create this data, they would want
 * to provide access to it. Also, DatabaseMigration to upgrade
 * the schema between releases.
 * 
 * @author matt
 *
 */
public interface NewDatabaseCreation {
    /**
     * @return an instance of the facade; this can be called
     * several times during creation; perhaps this could be a
     * single instance that's returned?
     */
    NewDatabaseCreationFacade getNewDatabaseCreationFacade();
}

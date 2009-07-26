package uk.me.gumbley.minimiser.pluginmanager.facade.newdatabase;

/**
 * Plugins that want to create database content after the File|New
 * wizard has collected user input must implement this and provide
 * the facade.
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

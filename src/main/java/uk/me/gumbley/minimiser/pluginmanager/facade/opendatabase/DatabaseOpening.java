package uk.me.gumbley.minimiser.pluginmanager.facade.opendatabase;


/**
 * Plugins that persist their own data and need a DAOFactory in
 * order to obtain access to it must implement this and provide
 * the facade.
 * <p>
 * It is highly likely that such plugins would also implement
 * NewDatabaseCreation - as they would want to create this data.
 * 
 * @author matt
 *
 */
public interface DatabaseOpening {
    /**
     * @return an instance of the facade; this can be called
     * several times during opening; perhaps this could be a
     * single instance that's returned?
     */
    DatabaseOpeningFacade getDatabaseOpeningFacade();
}

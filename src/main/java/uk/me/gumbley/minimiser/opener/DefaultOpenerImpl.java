package uk.me.gumbley.minimiser.opener;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.BadPasswordException;
import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.util.InstancePair;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * Default implementation of Opener.
 * 
 * @author matt
 *
 */
public final class DefaultOpenerImpl implements Opener {
    private static final Logger LOGGER = Logger.getLogger(DefaultOpenerImpl.class);
    
    private final AccessFactory access;
    private final ObserverList<DatabaseOpenEvent> observerList;


    /**
     * Construct the Opener.
     * @param accessFactory the access factory used for accessing databases
     */
    public DefaultOpenerImpl(final AccessFactory accessFactory) {
        this.access = accessFactory;
        observerList = new ObserverList<DatabaseOpenEvent>();
    }
    
    /**
     * {@inheritDoc}
     */
    
    public void addDatabaseOpenObserver(final Observer<DatabaseOpenEvent> observer) {
        observerList.addObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    
    public InstanceSet<DAOFactory> openDatabase(
            final String dbName,
            final String pathToDatabase,
            final OpenerAdapter openerAdapter) {
        openerAdapter.startOpening();
        LOGGER.info("Opening database '" + dbName + "' from path '" + pathToDatabase + "'");
        openerAdapter.reportProgress(ProgressStage.STARTING, "Starting to open '" + dbName + "'");

        // Try at first with an empty password - if we get a BadPasswordException,
        // prompt for password and retry.
        String dbPassword = "";
        String tryingToOpenMessage = "Opening database '" + dbName + "'";
        while (true) {
            try {
                openerAdapter.reportProgress(ProgressStage.OPENING, tryingToOpenMessage);
                final InstanceSet<DAOFactory> daoFactories = access.openDatabase(pathToDatabase, dbPassword);
                final MiniMiserDAOFactory miniMiserDAOFactory = daoFactories.getInstanceOf(MiniMiserDAOFactory.class);
                LOGGER.info("Opened OK");
        
                openerAdapter.reportProgress(ProgressStage.OPENED, "Opened '" + dbName + "' OK");
                openerAdapter.stopOpening();
                
                final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(dbName, pathToDatabase);
                // TODO: remove this legacy approach to getting the MiniMiserDAOFactory
                databaseDescriptor.setAttribute(AttributeIdentifier.Database, miniMiserDAOFactory);

                // Add the MiniMiserDAOFactory and other plugins'
                // DAOFactories to the DatabaseDescriptor
                for (final InstancePair<DAOFactory> daoFactoryPair : daoFactories.asList()) {
                    databaseDescriptor.setDAOFactory(daoFactoryPair.getClassOfInstance(), daoFactoryPair.getInstance());
                }
                
                observerList.eventOccurred(new DatabaseOpenEvent(databaseDescriptor));

                return daoFactories;
                
            } catch (final BadPasswordException bad) {
                LOGGER.warn("Bad password: " + bad.getMessage());
                openerAdapter.reportProgress(ProgressStage.PASSWORD_REQUIRED, "Password required for '" + dbName + "'");
                dbPassword = openerAdapter.requestPassword();
                if (dbPassword == null || dbPassword.equals("")) {
                    LOGGER.info("Open of encrypted database cancelled");
                    openerAdapter.reportProgress(ProgressStage.PASSWORD_CANCELLED, "Open of '" + dbName + "' cancelled");
                    openerAdapter.stopOpening();
                    return null;
                }
                
                tryingToOpenMessage = "Trying to open database '" + dbName + "'";
            } catch (final DataAccessResourceFailureException darfe) {
                LOGGER.warn("Could not open database: " + darfe.getMessage());
                openerAdapter.reportProgress(ProgressStage.NOT_PRESENT, "Database '" + dbName + "' not found");
                openerAdapter.databaseNotFound(darfe);
                openerAdapter.stopOpening();
                return null;
                
            } catch (final DataAccessException dae) {
                LOGGER.warn("Data access exception opening database: " + dae.getMessage(), dae);
                openerAdapter.reportProgress(ProgressStage.OPEN_FAILED, "Open of '" + dbName + "' failed");
                openerAdapter.seriousProblemOccurred(dae);
                openerAdapter.stopOpening();
                return null;
            }
        }
    }
}

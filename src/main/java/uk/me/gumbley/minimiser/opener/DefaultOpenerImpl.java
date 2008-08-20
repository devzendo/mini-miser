package uk.me.gumbley.minimiser.opener;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.BadPasswordException;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;

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
    
    public MiniMiserDatabase openDatabase(
            final String dbName,
            final String pathToDatabase,
            final OpenerAdapter openerAdapter) {
        openerAdapter.startOpening();
        LOGGER.info("Opening database '" + dbName + "' from path '" + pathToDatabase + "'");
        openerAdapter.reportProgress(ProgressStage.STARTING, "Starting to open '" + dbName + "'");

        // Try at first with an empty password - if we get a BPE, prompt for
        // password and retry.
        String dbPassword = "";
        String tryingToOpenMessage = "Opening database '" + dbName + "'";
        while (true) {
            try {
                openerAdapter.reportProgress(ProgressStage.OPENING, tryingToOpenMessage);
                final MiniMiserDatabase database = access.openDatabase(pathToDatabase, dbPassword);
                LOGGER.info("Opened OK");
        
                openerAdapter.reportProgress(ProgressStage.OPENED, "Opened '" + dbName + "' OK");
                openerAdapter.stopOpening();
                
                observerList.eventOccurred(new DatabaseOpenEvent(new MiniMiserDatabaseDescriptor(dbName, pathToDatabase, database)));

                return database;
                
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
                openerAdapter.reportProgress(ProgressStage.NOT_PRESENT, "Database " + dbName + "' not found");
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

package uk.me.gumbley.minimiser.gui.lifecycle;

import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.opener.OpenerAdapterFactory;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.util.DatabasePair;
import uk.me.gumbley.minimiser.util.DatabasePairEncapsulator;

/**
 * A Lifecycle that opens all databases saved in prefs, and switches to the
 * last active one.
 * 
 * @author matt
 *
 */
public final class DatabaseOpenerLifecycle implements Lifecycle {
    private static final Logger LOGGER = Logger
            .getLogger(DatabaseOpenerLifecycle.class);
    private final Opener opener;
    private final OpenerAdapterFactory openerAdapterFactory;
    private final Prefs prefs;
    private final OpenDatabaseList openDatabaseList;

    /**
     * We need to read the open database list so we know what to open on
     * startup.
     * @param openr the Opener
     * @param adapterFactory the OpenerAdaptorFactory
     * @param openList the OpenDatabaseList
     * @param prefstore the prefs store
     */
    public DatabaseOpenerLifecycle(final Opener openr,
            final OpenerAdapterFactory adapterFactory,
            final OpenDatabaseList openList,
            final Prefs prefstore) {
        this.opener = openr;
        this.openerAdapterFactory = adapterFactory;
        this.openDatabaseList = openList;
        this.prefs = prefstore;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        LOGGER.info("Opening all databases that were open on last shutdown");
        final String[] encapsulatedPairs = prefs.getOpenFiles();
        for (final String encapsulatedPair : encapsulatedPairs) {
            final DatabasePair databasePair = DatabasePairEncapsulator.unescape(encapsulatedPair);
            final OpenerAdapter openerAdapter = openerAdapterFactory.createOpenerAdapter(databasePair.getName());
            opener.openDatabase(databasePair.getName(), databasePair.getPath(), openerAdapter);
        }
        LOGGER.info("Previously open databases opened");
        
        final String lastActiveDatabaseName = prefs.getLastActiveFile();
        if (lastActiveDatabaseName == null) {
            LOGGER.info("No last active database recorded, so cannot switch");
        } else {
            LOGGER.info("Switching to last active database");
            openDatabaseList.switchDatabase(lastActiveDatabaseName);
            LOGGER.info("Switched to last active database");
        }
    }
}

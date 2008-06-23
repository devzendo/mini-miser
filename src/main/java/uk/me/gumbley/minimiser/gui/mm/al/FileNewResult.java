package uk.me.gumbley.minimiser.gui.mm.al;

import java.io.File;
import java.util.Map;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.odl.DatabaseDescriptor;
import uk.me.gumbley.minimiser.gui.odl.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.PersistenceObservableEvent;
import uk.me.gumbley.minimiser.util.WorkerPool;

public class FileNewResult extends DeferredWizardResult {
    private final WorkerPool pool;
    private final OpenDatabaseList databaseList;
    private final AccessFactory access;
    private final CursorManager cursorMan;

    public FileNewResult(final WorkerPool workerPool,
            final OpenDatabaseList openDatabaseList,
            final AccessFactory accessFactory,
            final CursorManager cursorManager) {
        this.pool = workerPool;
        this.databaseList = openDatabaseList;
        this.access = accessFactory;
        this.cursorMan = cursorManager;
    }
    private static final Logger LOGGER = Logger.getLogger(FileNewResult.class);
    private static final int SETUP_STEPS = 5;
    private static final int DB_CREATION_STEPS = 6; // See TestMigratableDatabase::testCreatePlaintextDatabaseWithListener
    private static final int MAX_STEPS = SETUP_STEPS + DB_CREATION_STEPS;
    
    // WOZERE need to hook into the accessfactory to get more detail on
    // database creation via a listener. Then when all working, remove the old
    // wizard creation cruft.
    @Override
    public void start(final Map map, final ResultProgressHandle progress) {
        final Map<String, Object> result = (Map<String, Object>) map;
        if (result == null) {
            LOGGER.info("User cancelled File|New");
            return;
        }
        int stepNo = 1;
        progress.setProgress("Starting creation", stepNo++, MAX_STEPS);
        cursorMan.hourglassViaEventThread();

        LOGGER.info("Result: " + result);
        for (String key : result.keySet()) {
            Object obj = result.get(key);
            LOGGER.info("key " + key + " value '" + obj + "' type " + obj.getClass().getSimpleName());
            
        }
        progress.setProgress("Converting params", stepNo++, MAX_STEPS);
        final FileNewParameters params = new FileNewParameters(
            (String) result.get(FileNewWizardChooseFolderPage.PATH_NAME),
            (Boolean) result.get(FileNewWizardSecurityOptionPage.ENCRYPTED),
            (String) result.get(FileNewWizardSecurityOptionPage.PASSWORD),
            (String) result.get(FileNewWizardCurrencyPage.CURRENCY));

        LOGGER.info("Creating database at " + params.getPath());
        // need the db path to be /path/to/db/databasename/databasename
        // i.e. duplicate the directory component at the end
        progress.setProgress("Getting path", stepNo++, MAX_STEPS);

        final File path = new File(params.getPath());
        final String dbName = path.getName();
        final String fullPath = StringUtils.slashTerminate(path.getAbsolutePath()) + dbName;
        LOGGER.info("Final db path is " + fullPath);
        progress.setProgress("Creating DB", stepNo++, MAX_STEPS);

        final Observer<PersistenceObservableEvent> observer = new Observer<PersistenceObservableEvent>() {
            public void eventOccurred(final PersistenceObservableEvent observableEvent) {
                progress.setProgress(observableEvent.getDescription(), 0 /* WOZERE stepNo++*/ , MAX_STEPS);
            }
        };
        final MiniMiserDatabase database = access.createDatabase(fullPath, params.isEncrypted() ? params.getPassword() : "");
        progress.setProgress("Updating GUI", stepNo++, MAX_STEPS);

        final Runnable swingTask = new Runnable() {
            public void run() {
                LOGGER.info("Database created; adding to open database list");
                // TODO the MiniMiserDatabase is now lost - need to add it
                // to the DatabaseDescriptor.
                databaseList.addOpenedDatabase(new DatabaseDescriptor(dbName));
                cursorMan.normal();
            }
        };
        GUIUtils.runOnEventThread(swingTask);
        progress.finished(null); // TODO create summary here?
    }
}

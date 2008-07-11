package uk.me.gumbley.minimiser.gui.menu.actionlisteners.filenew;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.springframework.dao.DataAccessException;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.odl.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;
import uk.me.gumbley.minimiser.persistence.PersistenceObservableEvent;

/**
 * The worker for creating databases when the File|New wizard has finished.
 * @author matt
 *
 */
public final class FileNewResult extends DeferredWizardResult {
    private static final Logger LOGGER = Logger.getLogger(FileNewResult.class);
    private static final int SETUP_STEPS = 6;
    private final OpenDatabaseList databaseList;
    private final AccessFactory access;
    private final CursorManager cursorMan;

    /**
     * Create the FileNewResult creation worker
     * @param openDatabaseList the open database list that will be added to
     * @param accessFactory the access factory to be used for creation
     * @param cursorManager allows us to set the hourglass/normal cursor
     */
    public FileNewResult(final OpenDatabaseList openDatabaseList,
            final AccessFactory accessFactory,
            final CursorManager cursorManager) {
        this.databaseList = openDatabaseList;
        this.access = accessFactory;
        this.cursorMan = cursorManager;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void start(final Map map, final ResultProgressHandle progress) {
        final Map<String, Object> result = (Map<String, Object>) map;
        if (result == null) {
            LOGGER.info("User cancelled File|New");
            return;
        }
        final int maxSteps = access.getNumberOfDatabaseCreationSteps() + SETUP_STEPS;
        final AtomicInteger stepNo = new AtomicInteger(1);
        progress.setProgress("Starting creation", stepNo.incrementAndGet(), maxSteps);
        cursorMan.hourglassViaEventThread();

        progress.setProgress("Converting params", stepNo.incrementAndGet(), maxSteps);
        final FileNewParameters params = new FileNewParameters(
            (String) result.get(FileNewWizardChooseFolderPage.PATH_NAME),
            (Boolean) result.get(FileNewWizardSecurityOptionPage.ENCRYPTED),
            (String) result.get(FileNewWizardSecurityOptionPage.PASSWORD),
            (String) result.get(FileNewWizardCurrencyPage.CURRENCY));

        LOGGER.info("Creating database at " + params.getPath());
        // need the db path to be /path/to/db/databasename/databasename
        // i.e. duplicate the directory component at the end
        progress.setProgress("Getting path", stepNo.incrementAndGet(), maxSteps);

        final File path = new File(params.getPath());
        final String dbName = path.getName();
        final String fullPath = StringUtils.slashTerminate(path.getAbsolutePath()) + dbName;
        LOGGER.info("Final db path is " + fullPath);
        progress.setProgress("Creating DB", stepNo.incrementAndGet(), maxSteps);

        final Observer<PersistenceObservableEvent> observer = new Observer<PersistenceObservableEvent>() {
            public void eventOccurred(final PersistenceObservableEvent observableEvent) {
                LOGGER.info("DB creation progress: " + observableEvent.getDescription());
                progress.setProgress(observableEvent.getDescription(), stepNo.incrementAndGet(), maxSteps);
            }
        };
        try {
            final MiniMiserDatabase database = access.createDatabase(fullPath, params.isEncrypted() ? params.getPassword() : "", observer);
            progress.setProgress("Updating GUI", stepNo.incrementAndGet(), maxSteps);
    
            final Runnable addDatabaseAndNormalCursorSwingTask = new Runnable() {
                public void run() {
                    LOGGER.info("Database created; adding to open database list");
                    databaseList.addOpenedDatabase(new MiniMiserDatabaseDescriptor(dbName, database));
                    cursorMan.normal();
                }
            };
            GUIUtils.runOnEventThread(addDatabaseAndNormalCursorSwingTask);
            progress.finished(null);
        } catch (final DataAccessException dae) {
            LOGGER.warn("Failed to create database " + dbName + ": " + dae.getMessage(), dae);
            throw dae;
        } finally {
            cursorMan.normalViaEventThread();
        }
    }

    private final class FileNewParameters {
        private final String newPath;
        private final boolean newEncrypted;
        private final String newPassword;
        private final String newCurrency;

        public FileNewParameters(final String path, final boolean encrypted, final String password, final String currency) {
            this.newPath = path;
            this.newEncrypted = encrypted;
            this.newPassword = password;
            this.newCurrency = currency;
        }

        public String getCurrency() {
            return newCurrency;
        }

        public String getPassword() {
            return newPassword;
        }

        public String getPath() {
            return newPath;
        }
        
        public boolean isEncrypted() {
            return newEncrypted;
        }
    }

}

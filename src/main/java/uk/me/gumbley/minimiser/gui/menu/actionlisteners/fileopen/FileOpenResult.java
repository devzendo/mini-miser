package uk.me.gumbley.minimiser.gui.menu.actionlisteners.fileopen;

import java.io.File;
import java.util.Map;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.springframework.dao.DataAccessException;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.odl.DatabaseDescriptor;
import uk.me.gumbley.minimiser.gui.odl.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.BadPasswordException;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;

/**
 * The worker for opening databases when the File|Open wizard has finished.
 * @author matt
 *
 */
public final class FileOpenResult extends DeferredWizardResult {
    private static final Logger LOGGER = Logger.getLogger(FileOpenResult.class);
    private static final int SETUP_STEPS = 5;
    private final OpenDatabaseList databaseList;
    private final AccessFactory access;
    private final CursorManager cursorMan;

    /**
     * Create the FileOpenResult creation worker
     * @param openDatabaseList the open database list that will be added to
     * @param accessFactory the access factory to be used for opening
     * @param cursorManager allows us to set the hourglass/normal cursor
     */
    public FileOpenResult(final OpenDatabaseList openDatabaseList,
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
            LOGGER.info("User cancelled File|Open");
            return;
        }
        cursorMan.hourglassViaEventThread();

        final String dbPath = (String) result.get(FileOpenWizardChooseFolderPage.PATH_NAME);
        // dbPath is the path to the directory - need to add the name of the directory
        // to the end, as that's the db name
        final File dbPathFile = new File(dbPath);
        final String dbName = dbPathFile.getName();
        final String dbFullPath = StringUtils.slashTerminate(dbPath) + dbName;
        LOGGER.info("Opening database at path: " + dbFullPath);
        try {
            final MiniMiserDatabase openMigratableDatabase = access.openDatabase(dbFullPath, "");
            LOGGER.info("Opened OK");

            final Runnable addDatabaseAndNormalCursorSwingTask = new Runnable() {
                public void run() {
                    LOGGER.info("Database created; adding to open database list");
                    // TODO the MiniMiserDatabase is now lost - need to add it
                    // to the DatabaseDescriptor.
                    databaseList.addOpenedDatabase(new DatabaseDescriptor(dbName));
                    cursorMan.normal();
                }
            };
            GUIUtils.runOnEventThread(addDatabaseAndNormalCursorSwingTask);
        } catch (final BadPasswordException bad) {
            // TODO get user to enter password and try again
            LOGGER.warn("Bad password: " + bad.getMessage());
        } catch (final DataAccessException dae) {
            // TODO report error to user
            LOGGER.warn("Data access exception: " + dae.getMessage());
        } finally {
            cursorMan.normalViaEventThread();
        }
        progress.finished(null); // TODO create summary here?
    }
}

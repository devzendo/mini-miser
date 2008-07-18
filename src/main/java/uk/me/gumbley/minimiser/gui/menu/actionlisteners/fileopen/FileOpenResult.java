package uk.me.gumbley.minimiser.gui.menu.actionlisteners.fileopen;

import java.io.File;
import java.util.Map;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.PasswordEntryDialogHelper;
import uk.me.gumbley.minimiser.gui.dialog.ProblemDialog;
import uk.me.gumbley.minimiser.gui.odl.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.BadPasswordException;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;

/**
 * The worker for opening databases when the File|Open wizard has finished.
 * @author matt
 *
 */
public final class FileOpenResult extends DeferredWizardResult {
    private static final Logger LOGGER = Logger.getLogger(FileOpenResult.class);
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
        progress.setProgress("Starting to open", 0, 3);
        final Map<String, Object> result = (Map<String, Object>) map;
        if (result == null) {
            LOGGER.info("User cancelled File|Open");
            return;
        }
        LOGGER.debug("start with " + result);
        cursorMan.hourglassViaEventThread();

        final String dbPath = (String) result.get(FileOpenWizardChooseFolderPage.PATH_NAME);
        // dbPath is the path to the directory - need to add the name of the directory
        // to the end, as that's the db name
        final File dbPathFile = new File(dbPath);
        final String dbName = dbPathFile.getName();
        final String dbFullPath = StringUtils.slashTerminate(dbPath) + dbName;
        LOGGER.info("Opening database at path: " + dbFullPath);
        boolean retryPasswordLoop = true;
        // Try at first with an empty password - if we get a BPE, prompt for
        // password and retry.
        String dbPassword = "";
        String tryingToOpenMessage = "Opening database";
        while (retryPasswordLoop) {
            try {
                progress.setProgress(tryingToOpenMessage, 1, 3);
                final MiniMiserDatabase database = access.openDatabase(dbFullPath, dbPassword);
                LOGGER.info("Opened OK");
                progress.setProgress("Opened OK", 3, 3);
                
                final Runnable addDatabaseSwingTask = new Runnable() {
                    public void run() {
                        LOGGER.info("Database created; adding to open database list");
                        databaseList.addOpenedDatabase(new MiniMiserDatabaseDescriptor(dbName, dbFullPath, database));
                    }
                };
                GUIUtils.runOnEventThread(addDatabaseSwingTask);
                // A small delay to allow the user to notice the
                // Opened OK progress result - otherwise the wizard just
                // vanishes a little too abruptly.
                ThreadUtils.waitNoInterruption(250);
                retryPasswordLoop = false;
            } catch (final BadPasswordException bad) {
                LOGGER.warn("Bad password: " + bad.getMessage());
                progress.setProgress("Password required", 2, 3);
                // TODO pass the wizard's frame in here
                final PasswordEntryDialogHelper passwordHelper = new PasswordEntryDialogHelper();
                dbPassword = passwordHelper.promptForPassword(null, dbName);
                if (dbPassword.equals("")) {
                    LOGGER.info("Cancelled open (with password)");
                    retryPasswordLoop = false;
                }
                tryingToOpenMessage = "Trying to open database";
            } catch (final DataAccessResourceFailureException darfe) {
                LOGGER.warn("Could not open database: " + darfe.getMessage());
                // TODO pass the wizard's frame in here
                GUIUtils.invokeLaterOnEventThread(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(null,
                            "Could not open database '" + dbName + "':\n" + darfe.getMessage(),
                            "Could not open database '" + dbName + "'",
                            JOptionPane.OK_OPTION);
                    }
                });
                retryPasswordLoop = false;
                // don't retry file choice - shouldn't have been able to open
                // rubbish anyway
            } catch (final DataAccessException dae) {
                // TODO report error to user
                // WOZERE the whole app goes ape if an exception is thrown, and clobbers eclipse
                
                LOGGER.warn("Data access exception: " + dae.getMessage(), dae);
                // TODO pass the wizard's frame in here
                ProblemDialog.reportProblem(null, "trying to open database '" + dbName + "'.", dae);
                retryPasswordLoop = false;
            }
        }
        progress.finished(null);
        cursorMan.normalViaEventThread();
    }
}

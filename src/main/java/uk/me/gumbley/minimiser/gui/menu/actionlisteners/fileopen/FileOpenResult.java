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
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
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
    
    private class FileOpenWizardOpenerAdapter implements OpenerAdapter {

        private String dbName;
        private final ResultProgressHandle progressHandle;

        public FileOpenWizardOpenerAdapter(final ResultProgressHandle progress, final String name) {
            this.progressHandle = progress;
            dbName = name;
        }

        public void reportProgress(final ProgressStage progressStage, final String description) {
            progressHandle.setProgress(description, progressStage.getValue(), progressStage.getMaximumValue());
        }

        public String requestPassword() {
            // TODO pass the wizard's frame in here
            final PasswordEntryDialogHelper passwordHelper = new PasswordEntryDialogHelper();
            return passwordHelper.promptForPassword(null, dbName);
        }
        
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

        final Opener opener = new Opener(access);
        final OpenerAdapter openerAdapter = new FileOpenWizardOpenerAdapter(progress, dbName);
        try {
            final MiniMiserDatabase database = opener.openDatabase(dbName, dbFullPath, openerAdapter);
            // TODO SMELL - this should just run, and the menu shoudl deal with dispatch on the EDT
            databaseList.addOpenedDatabase(new MiniMiserDatabaseDescriptor(dbName, dbFullPath, database));
//            final Runnable addDatabaseSwingTask = new Runnable() {
//                public void run() {
//                    LOGGER.info("Database created; adding to open database list");
//                    databaseList.addOpenedDatabase(new MiniMiserDatabaseDescriptor(dbName, dbFullPath, database));
//                }
//            };
//            GUIUtils.runOnEventThread(addDatabaseSwingTask);
            // A small delay to allow the user to notice the
            // Opened OK progress result - otherwise the wizard just
            // vanishes a little too abruptly.
            ThreadUtils.waitNoInterruption(250);
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
        } catch (final DataAccessException dae) {
            LOGGER.warn("Data access exception: " + dae.getMessage(), dae);
            // TODO pass the wizard's frame in here
            ProblemDialog.reportProblem(null, "trying to open database '" + dbName + "'.", dae);
        }
        progress.finished(null);
        cursorMan.normalViaEventThread();
    }
}

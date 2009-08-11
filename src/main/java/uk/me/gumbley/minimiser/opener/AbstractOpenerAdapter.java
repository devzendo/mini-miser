package uk.me.gumbley.minimiser.opener;

import java.awt.Frame;
import java.util.concurrent.Callable;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.gui.GUIValueObtainer;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.passwordentry.PasswordEntryDialogHelper;
import uk.me.gumbley.minimiser.gui.dialog.problem.ProblemDialogHelper;

/**
 * Provides the common app-wide facilities of an OpenerAdapter:
 * <ul>
 * <li> hourglass cursor on start
 * <li> PasswordEntryDialogHelper used for password prompts
 * <li> JOptionPane display of migration prompt
 * <li> JOptionPane display of database not found
 * <li> ProblemDialog display of serious problems
 * <li> normal cursor on stop
 * </ul>
 * 
 * @author matt
 *
 */
public abstract class AbstractOpenerAdapter implements OpenerAdapter {
    private static final Logger LOGGER = Logger
            .getLogger(AbstractOpenerAdapter.class);
    private final Frame parentFrame;
    private final String dbName;
    private final CursorManager cursorManager;

    /**
     * Construct the AbstractOpenerAdapter
     * @param frame the parent frame over which any dialogs would be displayed
     * @param databaseName the name of the database to open
     * @param cursorMgr the cursor manager
     */
    public AbstractOpenerAdapter(final Frame frame, final String databaseName, final CursorManager cursorMgr) {
        this.parentFrame = frame;
        this.dbName = databaseName;
        this.cursorManager = cursorMgr;
    }

    /**
     * {@inheritDoc}
     */
    public final String requestPassword() {
        final PasswordEntryDialogHelper passwordHelper = new PasswordEntryDialogHelper();
        return passwordHelper.promptForPassword(parentFrame, dbName);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean requestMigration() {
        final GUIValueObtainer<Boolean> obtainer = new GUIValueObtainer<Boolean>();
        try {
            return obtainer.obtainFromEventThread(new Callable<Boolean>() {

                public Boolean call() throws Exception {
                    final int opt = JOptionPane.showConfirmDialog(parentFrame,
                        "This database was created by an older version\n"
                        + "of the software, and must be upgraded before\n"
                        + "it can be used by this version.\n\n"
                        + "After upgrading, you will no longer be able\n"
                        + "to open it in older versions of the software.\n"
                        + "Upgrading cannot be undone.\n\n"
                        + "Choose Yes to upgrade the database, or\n"
                        + "No to leave the database alone and not open it",
                        "Confirm database upgrade",
                        JOptionPane.YES_NO_OPTION);
                    return opt == 0;
                }
                
            });
        } catch (final Exception e) {
            // it has been logged by the GUIValueObtainer
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void startOpening() {
        cursorManager.hourglassViaEventThread(this.getClass().getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    public final void databaseNotFound(final DataAccessResourceFailureException exception) {
        LOGGER.warn("Could not open database '" + dbName + "': " + exception.getMessage());
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(parentFrame,
                    "Could not open database '" + dbName + "'\n" + exception.getMessage(),
                    "Could not open database '" + dbName + "'",
                    JOptionPane.OK_OPTION);
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    public final void seriousProblemOccurred(final DataAccessException exception) {
        LOGGER.warn("Data access exception: " + exception.getMessage(), exception);
        ProblemDialogHelper.reportProblem("trying to open database '" + dbName + "'.", exception);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void stopOpening() {
        cursorManager.normalViaEventThread(this.getClass().getSimpleName());
    }
}

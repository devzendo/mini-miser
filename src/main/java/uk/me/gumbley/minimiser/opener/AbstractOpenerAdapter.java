package uk.me.gumbley.minimiser.opener;

import java.awt.Frame;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.PasswordEntryDialogHelper;

/**
 * Provides the common app-wide facilities of an OpenerAdapter:
 * <ul>
 * <li> hourglass cursor on start
 * <li> PasswordEntryDialogHelper used for password prompts
 * <li> JOptionPane display of database not found
 * <li> ProblemDialog display of serious problems
 * <li> normal cursor on stop
 * </ul>
 * 
 * @author matt
 *
 */
public abstract class AbstractOpenerAdapter implements OpenerAdapter {
    private Frame parentFrame;
    private final String dbName;
    private final CursorManager cursorManager;

    /**
     * Construct the AbstractOpenerAdapter
     * @param frame the parent frame over which any dialogs would be displayed
     * @param name the name of the database to open
     * @param cursorMgr the cursor manager
     */
    public AbstractOpenerAdapter(final Frame frame, final String name, final CursorManager cursorMgr) {
        this.parentFrame = frame;
        this.dbName = name;
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
    public final void startOpening() {
        cursorManager.hourglassViaEventThread();
    }

    /**
     * {@inheritDoc}
     */
    public final void stopOpening() {
        cursorManager.normalViaEventThread();
    }
}

package uk.me.gumbley.minimiser.gui;

import java.awt.Frame;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.gui.dialog.ProblemDialog;
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;

/**
 * Common GUI code for opening databases and adding them to the OpenDatabaseList.
 * Callers provide an OpenerAdapter to customise their usage to their progress
 * reporting mechanism, either in the open wizard, or the main status area.
 * 
 * TODO I'm wondering if this could all be pushed into the Opener?
 * 
 * The separation of concerns between persistance and display is achieved
 * with the Opener / OpenerAdapter (persistence / display). The only things
 * that are in here are:
 * * the addition to the OpenDatabaseList
 * * the displaying of not found / serious problems.
 * * encapsulation of hourglass cursor management. (done)
 *  
 * These last two could be moved to the OpenerAdapter, probably with an
 * abstract base class containing problem display and cursor management,
 * leaving progress update to two subclasses.
 * Pushing problem management into the OpenerAdapter means passing Exceptions
 * into OpenerAdapter method calls, rather than by having user code catch
 * them - but it's an approach used in the ProblemDialog.
 * 
 * The first - addition to the OpenDatabaseList could be moved into the Opener
 * although it does increase coupling. It'd have to move there or stay here,
 * and leaving it here makes this class rather thin.
 * 
 * @author matt
 *
 */
public class OpenerHelper {
    private static final Logger LOGGER = Logger.getLogger(OpenerHelper.class);
    private final AccessFactory accessFactory;
    private final OpenDatabaseList openDatabaseList;
    private final CursorManager cursorManager;

    /**
     * Construct an OpenerHelper with the necessery linkage to the components
     * it needs to do the open.
     * @param access the AccessFactory
     * @param databaseList the OpenDatabaseList
     * @param cursor the CursorManager
     */
    public OpenerHelper(final AccessFactory access, final OpenDatabaseList databaseList, final CursorManager cursor) {
        this.accessFactory = access;
        this.openDatabaseList = databaseList;
        this.cursorManager = cursor;
    }
    
    /**
     * Use the Opener to open the database, and if successful, to add it to the
     * OpenDatabaseList. Switch to an hourglass cursor around this operation.
     * @param parentFrame the parent frame over which any problem dialogs
     * will be displayed
     * @param dbName the database name, mostly for display purposes
     * @param dbFullPath the full path to the database
     * @param openerAdapter an OpenerAdapter to decouple progress updates from
     * the progress reporting mechanism
     * 
     */
    public void openWithOpener(final Frame parentFrame, final String dbName, final String dbFullPath, final OpenerAdapter openerAdapter) {
        cursorManager.hourglassViaEventThread();
        final Opener opener = new Opener(accessFactory);
        try {
            final MiniMiserDatabase database = opener.openDatabase(dbName, dbFullPath, openerAdapter);
            openDatabaseList.addOpenedDatabase(new MiniMiserDatabaseDescriptor(dbName, dbFullPath, database));
        } catch (final DataAccessResourceFailureException darfe) {
            LOGGER.warn("Could not open database: " + darfe.getMessage());
            GUIUtils.invokeLaterOnEventThread(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(parentFrame,
                        "Could not open database '" + dbName + "':\n" + darfe.getMessage(),
                        "Could not open database '" + dbName + "'",
                        JOptionPane.OK_OPTION);
                }
            });
        } catch (final DataAccessException dae) {
            LOGGER.warn("Data access exception: " + dae.getMessage(), dae);
            ProblemDialog.reportProblem(parentFrame, "trying to open database '" + dbName + "'.", dae);
        }
        cursorManager.normalViaEventThread();
    }

}

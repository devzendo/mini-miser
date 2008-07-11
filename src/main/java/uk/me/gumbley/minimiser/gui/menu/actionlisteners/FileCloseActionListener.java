package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.dialog.ProblemDialog;
import uk.me.gumbley.minimiser.gui.odl.DatabaseDescriptor;
import uk.me.gumbley.minimiser.gui.odl.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;

/**
 * Triggered by the File Close menu.
 * 
 * @author matt
 *
 */
public final class FileCloseActionListener implements ActionListener {
    private static final Logger LOGGER = Logger
            .getLogger(FileCloseActionListener.class);
    private final OpenDatabaseList databaseList;
    private final AccessFactory access;
    private final CursorManager cursorMan;

    /**
     * Construct the listener
     * @param openDatabaseList the open database list singleton
     * @param accessFactory the access factory singleton
     * @param cursorManager the cursor manager singleton
     */
    public FileCloseActionListener(final OpenDatabaseList openDatabaseList,
            final AccessFactory accessFactory,
            final CursorManager cursorManager) {
        super();
        this.databaseList = openDatabaseList;
        this.access = accessFactory;
        this.cursorMan = cursorManager;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(final ActionEvent e) {
        final DatabaseDescriptor currentDatabase = databaseList.getCurrentDatabase();
        if (currentDatabase == null) {
            LOGGER.warn("Closing a null database");
            return;
        }
        cursorMan.hourglassViaEventThread();
        try {
            if (currentDatabase instanceof MiniMiserDatabaseDescriptor) {
                final MiniMiserDatabaseDescriptor mmdd = (MiniMiserDatabaseDescriptor) currentDatabase;
                try {
                    mmdd.getDatabase().close();
                } catch (final DataAccessException dae) {
                    // TODO pass main frame in here
                    ProblemDialog.reportProblem(null, "while closing the '" + currentDatabase.getDatabaseName() + "' database", dae);
                }
            } else {
                LOGGER.error("Closing a non-MiniMiserDatabaseDescriptor");
            }
        } finally {
            databaseList.removeClosedDatabase(currentDatabase);
            cursorMan.normalViaEventThread();
        }
    }
}

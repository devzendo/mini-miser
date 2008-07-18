package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;

/**
 * Triggered by the File Close All menu.
 * 
 * @author matt
 *
 */
public final class FileCloseAllActionListener extends AbstractFileCloseActionListener implements ActionListener {
    private static final Logger LOGGER = Logger
            .getLogger(FileCloseAllActionListener.class);

    /**
     * Construct the listener
     * @param openDatabaseList the open database list singleton
     * @param cursorManager the cursor manager singleton
     */
    public FileCloseAllActionListener(final OpenDatabaseList openDatabaseList,
            final CursorManager cursorManager) {
        super(openDatabaseList, cursorManager);
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(final ActionEvent e) {
        getCursorMan().hourglassViaEventThread();
        try {
            final int numberOfDatabases = getDatabaseList().getNumberOfDatabases();
            LOGGER.info("Closing " + numberOfDatabases + " " + StringUtils.pluralise("database", numberOfDatabases));
            while (getDatabaseList().getNumberOfDatabases() > 0) {
                if (!closeCurrentDatabase()) {
                    LOGGER.info("Stopping the Close All");
                    return;
                }
            }
            LOGGER.info("All databases closed");
        } finally {
            getCursorMan().normalViaEventThread();
        }
    }
}

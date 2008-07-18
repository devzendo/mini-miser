package uk.me.gumbley.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;

/**
 * Triggered by the File Close menu.
 * 
 * @author matt
 *
 */
public final class FileCloseActionListener extends AbstractFileCloseActionListener implements ActionListener {
    private static final Logger LOGGER = Logger
            .getLogger(FileCloseActionListener.class);

    /**
     * Construct the listener
     * @param openDatabaseList the open database list singleton
     * @param cursorManager the cursor manager singleton
     */
    public FileCloseActionListener(final OpenDatabaseList openDatabaseList,
            final CursorManager cursorManager) {
        super(openDatabaseList, cursorManager);
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(final ActionEvent e) {
        getCursorMan().hourglassViaEventThread();
        try {
            if (!closeCurrentDatabase()) {
                LOGGER.info("Stopping the Close All");
                return;
            }
        } finally {
            getCursorMan().normalViaEventThread();
        }
    }
}

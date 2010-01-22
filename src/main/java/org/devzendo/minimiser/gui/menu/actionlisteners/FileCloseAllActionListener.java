package org.devzendo.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.string.StringUtils;
import org.devzendo.minimiser.gui.CursorManager;
import org.devzendo.minimiser.openlist.OpenDatabaseList;


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
        getCursorMan().hourglassViaEventThread(this.getClass().getSimpleName());
        try {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.currentThread().setName("AllCloser");
                        Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);

                        final int numberOfDatabases = getDatabaseList().getNumberOfDatabases();
                        LOGGER.info("Closing " + numberOfDatabases + " " + StringUtils.pluralise("database", numberOfDatabases));
                        while (getDatabaseList().getNumberOfDatabases() > 0) {
                            if (!closeCurrentDatabase()) {
                                LOGGER.info("Stopping the Close All");
                                return;
                            }
                        }
                        LOGGER.info("All databases closed");
                    
                    } catch (final Throwable t) {
                        LOGGER.error("Close All thread caught unexpected " + t.getClass().getSimpleName(), t);
                    } finally {
                        LOGGER.debug("Close All complete");
                    }
                }
            }).start();
        } finally {
            getCursorMan().normalViaEventThread(this.getClass().getSimpleName());
        }
    }
}

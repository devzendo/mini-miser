package org.devzendo.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.CursorManager;
import org.devzendo.minimiser.openlist.OpenDatabaseList;

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
        getCursorMan().hourglassViaEventThread(this.getClass().getSimpleName());
        try {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.currentThread().setName("Closer");
                        Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);

                        if (!closeCurrentDatabase()) {
                            LOGGER.info("Stopping the Close");
                            return;
                        }        
                    } catch (final Throwable t) {
                        LOGGER.error("Closer thread caught unexpected " + t.getClass().getSimpleName(), t);
                    } finally {
                        LOGGER.debug("Closer complete");
                    }
                }
            }).start();
        } finally {
            getCursorMan().normalViaEventThread(this.getClass().getSimpleName());
        }
    }
}

package uk.me.gumbley.minimiser.gui;

import javax.swing.JFrame;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.common.AppName;

/**
 * Main Frame Title controller.
 * 
 * @author matt
 *
 */
public final class DefaultMainFrameTitleImpl implements MainFrameTitle {
    private String databaseName;
    private final JFrame mainFrame;
    private Object lock;
    
    /**
     * Create the Main Frame Title controller, updating the main frame.
     * @param mainframe the main application frame
     */
    public DefaultMainFrameTitleImpl(final JFrame mainframe) {
        mainFrame = mainframe;
        lock = new Object();
        synchronized (lock) {
            databaseName = null;
        }
        update();
    }

    /**
     * {@inheritDoc}
     */
    public void clearCurrentDatabaseName() {
        synchronized (lock) {
            databaseName = null;
        }
        update();
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentDatabaseName() {
        synchronized (lock) {
            return databaseName;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentDatabaseName(final String databasename) {
        synchronized (lock) {
            databaseName = databasename;
        }
        update();
    }

    private void update() {
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                final StringBuilder title = new StringBuilder(AppName.getAppName());
                synchronized (lock) {
                    if (databaseName != null) {
                        title.append(" - ");
                        title.append(databaseName);
                    }
                }
                mainFrame.setTitle(title.toString());
            }
        });
    }
}

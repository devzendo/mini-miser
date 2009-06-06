package uk.me.gumbley.minimiser.gui;

import javax.swing.JFrame;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;

/**
 * Main Frame Title controller.
 * 
 * @author matt
 *
 */
public final class DefaultMainFrameTitleImpl implements MainFrameTitle {
    private String mDatabaseName;
    private final JFrame mMainFrame;
    private final Object mLock;
    private final PluginRegistry mPluginRegistry;
    
    /**
     * Create the Main Frame Title controller, updating the main frame.
     * @param mainframe the main application frame
     * @param pluginRegistry the plugin registry, used to get
     * the name and version of the main application
     */
    public DefaultMainFrameTitleImpl(final JFrame mainframe, 
            final PluginRegistry pluginRegistry) {
        mMainFrame = mainframe;
        mPluginRegistry = pluginRegistry;
        mLock = new Object();
        synchronized (mLock) {
            mDatabaseName = null;
        }
        update();
    }

    /**
     * {@inheritDoc}
     */
    public void clearCurrentDatabaseName() {
        synchronized (mLock) {
            mDatabaseName = null;
        }
        update();
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentDatabaseName() {
        synchronized (mLock) {
            return mDatabaseName;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentDatabaseName(final String databasename) {
        synchronized (mLock) {
            mDatabaseName = databasename;
        }
        update();
    }

    private void update() {
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                final StringBuilder title = new StringBuilder(
                    mPluginRegistry.getApplicationName());
                synchronized (mLock) {
                    if (mDatabaseName != null) {
                        title.append(" - ");
                        title.append(mDatabaseName);
                    }
                }
                mMainFrame.setTitle(title.toString());
            }
        });
    }
}

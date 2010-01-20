package org.devzendo.minimiser.gui;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.gui.GUIUtils;

/**
 * Main Frame Title controller.
 * 
 * @author matt
 *
 */
public final class DefaultMainFrameTitleImpl implements MainFrameTitle {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultMainFrameTitleImpl.class);
    private String mDatabaseName;
    private String mApplicationName;
    private final JFrame mMainFrame;
    private final Object mLock;
    
    /**
     * Create the Main Frame Title controller, updating the main frame.
     * @param mainframe the main application frame
     */
    public DefaultMainFrameTitleImpl(final JFrame mainframe) {
        LOGGER.debug("Starting Main Frame Title");
        mMainFrame = mainframe;
        mLock = new Object();
        synchronized (mLock) {
            mDatabaseName = null;
            mApplicationName = null;
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
                final StringBuilder title = new StringBuilder();
                synchronized (mLock) {
                    if (mApplicationName != null) {
                        title.append(mApplicationName);
                    }
                    if (mApplicationName != null & mDatabaseName != null) {
                        title.append(" - ");
                    }
                    if (mDatabaseName != null) {
                        title.append(mDatabaseName);
                    }
                }
                mMainFrame.setTitle(title.toString());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationName(final String applicationName) {
        synchronized (mLock) {
            mApplicationName = applicationName;
        }
        update();
    }
}

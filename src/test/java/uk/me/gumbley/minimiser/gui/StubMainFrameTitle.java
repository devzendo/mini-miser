package uk.me.gumbley.minimiser.gui;

/**
 * Stub main frame title controller.
 * 
 * @author matt
 *
 */
public final class StubMainFrameTitle implements MainFrameTitle {
    private String mDatabaseName = null;
    private String mApplicationName = null;

    /**
     * {@inheritDoc}
     */
    public void clearCurrentDatabaseName() {
        mDatabaseName = null;
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentDatabaseName() {
        return mDatabaseName;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentDatabaseName(final String databasename) {
        mDatabaseName = databasename;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationName(final String applicationName) {
        mApplicationName = applicationName;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return mApplicationName;
    }
}

package uk.me.gumbley.minimiser.gui;

/**
 * Stub main frame title controller.
 * 
 * @author matt
 *
 */
public final class PluginRegistryAwareStubMainFrameTitle implements MainFrameTitle {
    private String databaseName = null;

    /**
     * {@inheritDoc}
     */
    public void clearCurrentDatabaseName() {
        databaseName = null;
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentDatabaseName() {
        return databaseName;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentDatabaseName(final String databasename) {
        databaseName = databasename;
    }
}

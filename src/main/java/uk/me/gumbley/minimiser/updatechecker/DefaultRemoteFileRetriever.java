package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;

import uk.me.gumbley.minimiser.pluginmanager.PluginManager;

/**
 * Obtains files over a HTTP connection, handling redirects
 * automatically.
 * @author matt
 *
 */
public final class DefaultRemoteFileRetriever implements RemoteFileRetriever {

    private final String mBaseUrl;

    /**
     * @param baseUrl the base URL of the update site
     */
    public DefaultRemoteFileRetriever(final PluginManager pluginManager) {
        this.mBaseUrl = pluginManager.getUpdateSiteBaseURL();
    }

    /**
     * {@inheritDoc}
     */
    public String getFileContents(final String fileName) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public File saveFileContents(final String fileName) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
}

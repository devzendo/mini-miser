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
     * @param pluginManager the plugin manager
     */
    public DefaultRemoteFileRetriever(final PluginManager pluginManager) {
        this.mBaseUrl = pluginManager.getUpdateSiteBaseURL();
    }

    /**
     * {@inheritDoc}
     */
    public String getFileContents(String updateBaseURL, final String fileName) throws IOException {
        throw new IOException("Download not yet written");
    }

    /**
     * {@inheritDoc}
     */
    public File saveFileContents(String updateBaseURL, final String fileName) throws IOException {
        throw new IOException("Download not yet written");
    }
}

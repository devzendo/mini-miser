package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;


/**
 * Obtains files over a HTTP connection, handling redirects
 * automatically.
 * @author matt
 *
 */
public final class DefaultRemoteFileRetriever implements RemoteFileRetriever {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultRemoteFileRetriever.class);
    /**
     */
    public DefaultRemoteFileRetriever() {
    }

    public static HttpHost convertURLtoHttpHost(final String url) throws IOException {
        final URL internalURL = new URL(url);
        return new HttpHost(internalURL.getHost(), internalURL.getPort());
    }
    /**
     * {@inheritDoc}
     */
    public String getFileContents(final String updateBaseURL, final String fileName) throws IOException {
        LOGGER.info("Performing HTTP GET for " + updateBaseURL + " - " + fileName);
        final HttpClient httpClient = new HttpClient();
        final HostConfiguration hostConfiguration = new HostConfiguration();
        final HttpHost httpHost = convertURLtoHttpHost(updateBaseURL);
        LOGGER.info("HttpHost is " + httpHost);
        hostConfiguration.setHost(httpHost);
        final HttpMethod method = new GetMethod(fileName);
        method.setFollowRedirects(true);
        final int result = httpClient.executeMethod(hostConfiguration, method);
        LOGGER.debug("Result is " + result);
        final String responseString = method.getResponseBodyAsString();
        LOGGER.debug("Response as string: '" + responseString + "'");
        return responseString;
    }

    /**
     * {@inheritDoc}
     */
    public File saveFileContents(String updateBaseURL, final String fileName) throws IOException {
        throw new IOException("Download not yet written");
    }
}

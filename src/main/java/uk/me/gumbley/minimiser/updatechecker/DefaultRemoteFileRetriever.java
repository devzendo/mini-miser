package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.UnknownHostException;

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
     * {@inheritDoc}
     */
    public String getFileContents(final String updateBaseURL, final String fileName) throws IOException {
        LOGGER.info("Performing HTTP GET for " + updateBaseURL + " - " + fileName);

        final HostConfiguration hostConfiguration = createHostConfiguration(updateBaseURL);
        final HttpMethod method = createGetMethod(fileName);
        final HttpClient httpClient = new HttpClient();
        try {
            final int result = httpClient.executeMethod(hostConfiguration, method);
            LOGGER.debug("Result is " + result);
            final String responseString = method.getResponseBodyAsString();
            LOGGER.debug("Response as string: '" + responseString + "'");
            return responseString;
        } catch (final UnknownHostException e) {
            throw convertUnknownHostExceptionToUsefulIOException(e);
        } catch (final IOException e) {
            LOGGER.warn("Could not perform HTTP GET: " + e.getMessage(), e);
            throw e;
        }
    }

    private IOException convertUnknownHostExceptionToUsefulIOException(
            final UnknownHostException e) {
        LOGGER.warn("Unknown host: " + e.getMessage());
        // Just having the host name as the exception text is not helpful
        return new IOException("The '" + e.getMessage() + " server is unknown");
    }

    private HttpMethod createGetMethod(final String fileName) {
        final HttpMethod method = new GetMethod(fileName);
        method.setFollowRedirects(true);
        return method;
    }

    private HostConfiguration createHostConfiguration(final String updateBaseURL)
            throws IOException {
        final URL internalURL = new URL(updateBaseURL);
        final HttpHost httpHost = new HttpHost(internalURL.getHost(), internalURL.getPort());
        LOGGER.info("HttpHost is " + httpHost);

        final HostConfiguration hostConfiguration = new HostConfiguration();
        hostConfiguration.setHost(httpHost);
        return hostConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    public File saveFileContents(final String updateBaseURL, final String fileName) throws IOException {
        final File tempFile = File.createTempFile("minimiser-download", ".txt");
        final FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

        try {
            LOGGER.info("Performing HTTP GET for " + updateBaseURL + " - " + fileName);

            final HostConfiguration hostConfiguration = createHostConfiguration(updateBaseURL);
            final HttpMethod method = createGetMethod(fileName);
            final HttpClient httpClient = new HttpClient();
            try {
                final int result = httpClient.executeMethod(hostConfiguration, method);
                LOGGER.debug("Result is " + result);
                final InputStream responseBodyAsStream = method.getResponseBodyAsStream();
                try {
                    copyStream(responseBodyAsStream, fileOutputStream);
                } finally {
                    responseBodyAsStream.close();
                }
            } catch (final UnknownHostException e) {
                throw convertUnknownHostExceptionToUsefulIOException(e);
            } catch (final IOException e) {
                LOGGER.warn("Could not perform HTTP GET: " + e.getMessage(), e);
                throw e;
            }
        } finally {
            fileOutputStream.close();
        }

        return tempFile;
    }

    private void copyStream(
            final InputStream readStream,
            final OutputStream writeStream) throws IOException {
        final int bufsize = 16384;
        final byte[] buf = new byte[bufsize];
        int nread;
        try {
            while ((nread = readStream.read(buf, 0, bufsize)) != -1) {
                writeStream.write(buf, 0, nread);
            }
        } catch (final IOException e) {
            LOGGER.warn("Could not copy streams: " + e.getMessage());
            throw e;
        }        
    }
}

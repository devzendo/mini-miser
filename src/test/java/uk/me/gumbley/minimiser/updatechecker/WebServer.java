package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

/**
 * A simple web server, for integration style tests.
 * 
 * @author matt
 *
 */
public final class WebServer implements Container {
    private static final Logger LOGGER = Logger.getLogger(WebServer.class);
    private volatile boolean mAlive;
    private Connection mConnection;
    private final HashMap<String, Map<String, String>> mData;
    
    /**
     * Create a web server for the given base URL on the given port
     * @param port the port at which the server can be contacted
     * @return
     * @throws IOException
     */
    public static WebServer createServer(final int port) throws IOException {
        final WebServer container = new WebServer();
        Connection connection = new SocketConnection(container);
        SocketAddress address = new InetSocketAddress(port);

        connection.connect(address);
        container.setConnection(connection);
        return container;
    }

    /**
     * @throws IOException on server error
     */
    private WebServer() throws IOException {
        mData = new HashMap<String, Map<String,String>>();
    }
    
    private void setConnection(final Connection connection) {
        mConnection = connection;
    }

    /**
     * Stop the server, close listening sockets, etc.
     */
    public void stop() {
        try {
            mConnection.close();
        } catch (final IOException e) {
            LOGGER.warn("Closing WebServer caught IOException: " + e.getMessage(), e);
        }
    }

    /**
     * Serve the given contents for the given file, acting as a
     * server for multiple URLs. i.e. the same file name could
     * return different contents for different URLs.
     * @param baseURL the URL to serve this file/contents for
     * @param fileName the name of the file
     * @param contents its contents
     */
    public void serveFileContents(
            final String baseURL,
            final String fileName,
            final String contents) {
        Map<String, String> serverData;
        if (mData.containsKey(baseURL)) {
            serverData = mData.get(baseURL);
        } else {
            serverData = new HashMap<String, String>();
            mData.put(baseURL, serverData);
        }
        serverData.put(fileName, contents);
    }

    /**
     * {@inheritDoc}
     */
    public void handle(final Request request, final Response response) {
        try {
            final long time = System.currentTimeMillis();
            
            response.set("Content-Type", "text/plain");
            response.set("Server", "WebServer/1.0 (Simple 4.0)");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);
            
            final PrintStream body = response.getPrintStream();
            final String target = request.getTarget();
            final String output = getResponseOutput(target);
            LOGGER.debug("Target: " + target + " output: '" + output + "'");
            if (output == null) {
                response.setCode(404);
            } else {
                body.print(output);
            }
            body.close();
        } catch (final IOException e) {
            LOGGER.warn("WebServer caught IOException: " + e.getMessage(), e);
        }
     }

    /**
     * We can't (yet) differentiate between requests for different
     * servers, so respond with the first that requests this
     * target
     * @param target the target path, e.g. /version.txt - note
     * that paths are stored without the starting slash
     * @return null if this target isn't stored for any server, or
     * the text to respond with, if it is stored for one.
     */
    private String getResponseOutput(final String target) {
        final String targetAsContent = target.startsWith("/") ?
                target.substring(1) : target;
            
        for (String baseURL : mData.keySet()) {
            final Map<String, String> contentMap = mData.get(baseURL);
            if (contentMap.containsKey(targetAsContent)) {
                return contentMap.get(targetAsContent);
            }
        }
        return null;
    } 
}

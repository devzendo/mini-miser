package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import javax.net.ServerSocketFactory;

import org.apache.log4j.Logger;

/**
 * A simple web server, for integration style tests.
 * 
 * @author matt
 *
 */
public final class WebServer {
    private static final Logger LOGGER = Logger.getLogger(WebServer.class);
    private volatile boolean mAlive;
    
    
    /**
     * @param baseURL the base URL - the port that the server is
     * bound to is retrieved from here.
     * @throws IOException on server error
     */
    public WebServer(final String baseURL) throws IOException {
        mAlive = true;
        final URL url = new URL(baseURL);
        final ServerSocketFactory socketFactory = ServerSocketFactory.getDefault();
        final ServerSocket serverSocket = socketFactory.createServerSocket(url.getPort());
        Thread listenThread = new Thread(new Runnable() {

            public void run() {
                LOGGER.info("Web server listening on port " + url.getPort());
                while (mAlive) {
                    try {
                        final Socket acceptSocket = serverSocket.accept();
                        //acceptSocket.
                    } catch (final IOException e) {
                        LOGGER.warn("Caught exception on accept: " + e.getMessage());
                    }
                }
            } });
    }

    /**
     * Stop the server, close listening sockets, etc.
     */
    public void stop() {
        // TODO Auto-generated method stub
        
    }

    /**
     * Serve the given contents for the given file.
     * @param fileName the name of the file
     * @param contents its contents
     */
    public void serveFileContents(final String fileName, final String contents) {
        // TODO Auto-generated method stub
        
    }
}

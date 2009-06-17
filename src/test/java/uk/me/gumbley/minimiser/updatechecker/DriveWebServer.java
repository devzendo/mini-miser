package uk.me.gumbley.minimiser.updatechecker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.logging.Logging;
import uk.me.gumbley.commoncode.resource.ResourceLoader;
import uk.me.gumbley.minimiser.gui.Beautifier;

/**
 * create a web server
 * @author matt
 *
 */
public final class DriveWebServer {
    private static final Logger LOGGER = Logger.getLogger(DriveWebServer.class);
    private static int port = 8080;
    
    private DriveWebServer() {
        // do nowt
    };
    
    /**
     * @param args the command line
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure();
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        argList = Logging.getInstance().setupLoggingFromArgs(argList);
        
        try {
            final WebServer server = WebServer.createServer(port);
            final String baseURL = "http://localhost:" + port;
            server.serveFileContents(baseURL, UpdateChecker.VERSION_NUMBER_FILE, "8.9");
            server.serveFileContents(baseURL, UpdateChecker.CHANGE_LOG_FILE,
                ResourceLoader.readResource("uk/me/gumbley/minimiser/updatechecker/futurechangelog.txt"));

            GUIUtils.runOnEventThread(new Runnable() {

                public void run() {
                    LOGGER.info("WebServer experimentation starting...");
                    Beautifier.makeBeautiful();
                    final JFrame frame = new JFrame("Web Server");
                    final JButton button = new JButton("Stop Web Server on port " + port);
                    button.addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent arg0) {
                            server.stop();
                            frame.dispose();
                            System.exit(0);
                        }
                    });
                    frame.add(button);
                    frame.pack();
                    frame.setVisible(true);
                }
            });

        } catch (final IOException e) {
            LOGGER.warn("Caught", e);
            System.exit(1);
        }
    }
}

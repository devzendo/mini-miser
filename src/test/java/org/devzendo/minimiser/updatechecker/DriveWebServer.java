/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.updatechecker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.Beautifier;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commoncode.logging.Logging;
import org.devzendo.commoncode.resource.ResourceLoader;


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
                ResourceLoader.readResource("org/devzendo/minimiser/updatechecker/futurechangelog.txt"));

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

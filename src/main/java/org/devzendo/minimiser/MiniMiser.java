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

package org.devzendo.minimiser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.exception.AppException;
import org.devzendo.commoncode.gui.GUIUtils;
import org.devzendo.commoncode.gui.ThreadCheckingRepaintManager;
import org.devzendo.commoncode.logging.Logging;
import org.devzendo.commoncode.string.StringUtils;
import org.devzendo.commonspring.springloader.SpringLoader;
import org.devzendo.commonspring.springloader.SpringLoaderFactory;
import org.devzendo.minimiser.gui.Beautifier;
import org.devzendo.minimiser.gui.MainFrame;
import org.devzendo.minimiser.gui.SqlConsoleFrame;
import org.devzendo.minimiser.pluginmanager.AppDetailsPropertiesLoader;
import org.devzendo.minimiser.prefs.PrefsFactory;
import org.devzendo.minimiser.prefs.PrefsLocation;


/**
 * The start of the application, parsescommand line for logging, and constructs
 * the main frame.
 * 
 * @author matt
 *
 */
public final class MiniMiser {
    private static final Logger LOGGER = Logger.getLogger(MiniMiser.class);
    
    /**
     * Thou shalt not instantiate
     */
    private MiniMiser() {
        // nothing
    }

    private static void initialisePrefs(final SpringLoader springLoader) {
        final PrefsLocation prefsLocation = springLoader.getBean("prefsLocation", PrefsLocation.class);
        LOGGER.debug("Prefs directory is " + prefsLocation.getPrefsDir().getAbsolutePath());
        LOGGER.debug("Prefs file is " + prefsLocation.getPrefsFile().getAbsolutePath());
        if (!prefsLocation.prefsDirectoryExists()) {
            LOGGER.info(String.format("Prefs directory %s does not exist - creating it",
                prefsLocation.getPrefsDir().getAbsolutePath()));
            if (!prefsLocation.createPrefsDirectory()) {
                LOGGER.warn("Failed to create prefs directory");
                GUIUtils.runOnEventThread(new Runnable() {
                    public void run() {
                        showPrefsDirCreationFailureMessage(prefsLocation);
                    }
                });
            } else {
                LOGGER.info("Created prefs directory OK");
            }
        }
        final PrefsFactory prefsFactory = springLoader.getBean("&prefs", PrefsFactory.class);
        prefsFactory.setPrefs(prefsLocation.getPrefsFile().getAbsolutePath());
    }

    private static void showPrefsDirCreationFailureMessage(final PrefsLocation prefsLocation) {
        JOptionPane.showMessageDialog(null, 
            // NOTE user-centric message
            // I18N
            String.format("The '%s' folder cannot be created - the application cannot continue.\n"
                + "This folder would be used to remember your options and settings.\n\n"
                + "Failure to create this folder may be be due to security permissions, or a full disk.",
                prefsLocation.getPrefsDir().getAbsolutePath()),
            "Could not create settings folder",
            JOptionPane.ERROR_MESSAGE);
        
        System.exit(0);
    }

    /**
     * The main class can either display...
     *
     */
    enum Operation {
        /**
         * The main application window
         */
        MainWindow,
        /**
         * The standalone SQL diagnostic console
         */
        SqlConsole
    };
    
    /**
     * @param args the command line args
     */
    public static void main(final String[] args) {
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        argList = Logging.getInstance().setupLoggingFromArgs(argList);
        LOGGER.info("Framework starting...");
        
        final String javaLibraryPath = System.getProperty("java.library.path");
        LOGGER.debug("java.library.path is '" + javaLibraryPath + "'");
        final File quaqua = new File(javaLibraryPath, "libquaqua.jnilib");
        LOGGER.debug("Quaqua JNI library exists there (for Mac OS X)? " + quaqua.exists());
        
        final ArrayList<String> finalArgList = argList;
        final SpringLoader springLoader = initSpringLoader();

        // Not doing anything else with the framework name/version
        // for now, just logging it.
        new AppDetailsPropertiesLoader();
        
        //
        // Sun changed their recommendations and now recommends the UI be built
        // on the EDT, so I think flagging creation on non-EDT is OK.
        // "We used to say that you could create the GUI on the main thread as
        // long as you didn't modify components that had already been realized.
        // While this worked for most applications, in certain situations it
        // could cause problems."
        // http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
        // So let's create it on the EDT anyway
        //
        ThreadCheckingRepaintManager.initialise();
        initialisePrefs(springLoader);

        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                try {
                    Beautifier.makeBeautiful();

                    // Process command line
                    Operation op = Operation.MainWindow;
                    for (int i = 0; i < finalArgList.size(); i++) {
                        final String arg = finalArgList.get(i);
                        LOGGER.debug("arg " + i + " = '" + arg + "'");
                        if (arg.equals("-sqlconsole")) {
                            op = Operation.SqlConsole;
                        }
                    }

                    if (op == Operation.MainWindow) {
                        new MainFrame(springLoader, finalArgList);
                    } else if (op == Operation.SqlConsole) {
                        new SqlConsoleFrame(springLoader, finalArgList);
                    }
                } catch (final AppException e) {
                    LOGGER.fatal(e.getMessage());
                    System.exit(1);
                }
            }

        });
    }
  
    private static SpringLoader initSpringLoader() {
        // Now load up Spring...
        final long startSpring = System.currentTimeMillis();
        final SpringLoader sl = SpringLoaderFactory
                .initialise(MiniMiserApplicationContexts.getApplicationContexts());
        final long stopSpring = System.currentTimeMillis();
        final long springElapsed = stopSpring - startSpring;
        LOGGER.debug("SpringLoader initialised in "
                + StringUtils.translateTimeDuration(springElapsed));
        return sl;
    }
}

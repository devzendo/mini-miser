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

package org.devzendo.minimiser.gui.dialog.dstamessage;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.devzendo.commoncode.logging.Logging;
import org.devzendo.commongui.Beautifier;
import org.devzendo.commongui.GUIUtils;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.prefs.TestPrefs;



/**
 * @author matt
 *
 */
public final class DriveDSTAMessage {

    private static final Logger LOGGER = Logger
            .getLogger(DriveDSTAMessage.class);
    
    private final MiniMiserPrefs prefs;
    private final File prefsFile;
    private final DSTAMessageFactory messageFactory;
    
    private DriveDSTAMessage() throws IOException {
        final JFrame frame = new JFrame("title");
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        final JButton trigger = new JButton("Message");
        trigger.addActionListener(new ActionListener () {
            public void actionPerformed(final ActionEvent e) {
                showMessage();
            }
        });
        
        frame.add(trigger);
        
        final JButton clear = new JButton("Clear DSTA Flags");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                prefs.clearAllDontShowThisAgainFlags();
            }
        });
        
        frame.add(clear);
        
        frame.pack();
        frame.setVisible(true);
        
        prefs = TestPrefs.createUnitTestPrefsFile();
        prefsFile = new File(prefs.getAbsolutePath());
        LOGGER.info("Prefs file is at " + prefsFile.getAbsolutePath());
        prefsFile.deleteOnExit();
        
        messageFactory = new DefaultDSTAMessageFactoryImpl(prefs, frame);
    }

    /**
     * 
     */
    protected void showMessage() {
        messageFactory.possiblyShowMessage(DSTAMessageId.TEST,
            "Here is a component containing text.\nThere are several lines...\n\nAnd they can get quite wide also, which is nice.");
    }

    /**
     * @param args command line
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure();
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        argList = Logging.getInstance().setupLoggingFromArgs(argList);
        

        GUIUtils.runOnEventThread(new Runnable() {

            public void run() {
                LOGGER.info("SQL Tab experimentation starting...");
                Beautifier.makeBeautiful();
                try {
                    new DriveDSTAMessage();
                } catch (final IOException e) {
                    LOGGER.warn("Couldn't create test prefs: " + e.getMessage(), e);
                }
            }
        });
    }
}

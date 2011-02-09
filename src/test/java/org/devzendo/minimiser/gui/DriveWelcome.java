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

package org.devzendo.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;
import org.devzendo.commonapp.gui.Beautifier;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.DefaultCursorManager;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commoncode.logging.Logging;
import org.devzendo.minimiser.gui.dialog.welcome.WelcomeDialog;
import org.devzendo.minimiser.pluginmanager.DummyAppPluginRegistry;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;



/**
 * @author matt
 *
 */
public final class DriveWelcome {
    private DriveWelcome() {
        // nop
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
                Beautifier.makeBeautiful();
                
                final JFrame frame = new JFrame("title");
                frame.setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setPreferredSize(new Dimension(800, 600));
                
                final CursorManager cursorManager = new DefaultCursorManager();
                cursorManager.setMainFrame(frame);
                
                final JButton launch = new JButton("Launch Welcome");
                launch.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        final PluginRegistry pluginRegistry = new DummyAppPluginRegistry();
                        final WelcomeDialog welcome = new WelcomeDialog(frame, cursorManager, pluginRegistry, true);
                        welcome.postConstruct();
                        welcome.pack();
                        welcome.setVisible(true);
                    }
                });
                frame.add(launch, BorderLayout.NORTH);
                frame.pack();
                frame.setVisible(true);
            }
            
        });
    }
}

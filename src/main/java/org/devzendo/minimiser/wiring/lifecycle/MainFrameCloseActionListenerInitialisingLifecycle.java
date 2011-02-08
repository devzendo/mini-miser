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

package org.devzendo.minimiser.wiring.lifecycle;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commonapp.lifecycle.Lifecycle;
import org.devzendo.commonapp.spring.springloader.SpringLoader;


/**
 * A Lifecycle that initialises the close action listener
 * and attaches it to the main frame.
 * 
 * Non-TDD.
 * 
 * @author matt
 *
 */
public final class MainFrameCloseActionListenerInitialisingLifecycle implements Lifecycle {
    private static final Logger LOGGER = Logger
            .getLogger(MainFrameCloseActionListenerInitialisingLifecycle.class);
    private final SpringLoader mSpringLoader;

    /**
     * Construct
     * @param springLoader the SpringLoader.
     */
    public MainFrameCloseActionListenerInitialisingLifecycle(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                LOGGER.debug("Setting the main frame's Close ActionListener");
                final JFrame mainFrame = mSpringLoader.getBean("mainFrame", JFrame.class);
                final ActionListener closeActionListener = mSpringLoader.getBean("mainFrameCloseActionListener", ActionListener.class);
                mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                mainFrame.addWindowListener(new WindowListener() {
                    public void windowOpened(final WindowEvent e) {
                    }

                    public void windowClosing(final WindowEvent e) {
                        closeActionListener.actionPerformed(null);
                    }

                    public void windowClosed(final WindowEvent e) {
                    }

                    public void windowIconified(final WindowEvent e) {
                    }

                    public void windowDeiconified(final WindowEvent e) {
                    }

                    public void windowActivated(final WindowEvent e) {
                    }

                    public void windowDeactivated(final WindowEvent e) {
                    }
                });
                LOGGER.debug("The main frame's Close ActionListener is set");
            }
        });
    }
}

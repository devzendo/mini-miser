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

package org.devzendo.minimiser.gui.dialog.problem;

import java.awt.Frame;

import org.devzendo.commongui.GUIUtils;
import org.devzendo.commonspring.springloader.SpringLoader;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;


/**
 * Toolkit for easily creating Problem Dialogs.
 * 
 * @author matt
 *
 */
public final class ProblemDialogHelper {
    private static Frame parentFrame;
    private static PluginRegistry pluginRegistry;

    /**
     * 
     */
    private ProblemDialogHelper() {
        // no instances
    }
    
    /**
     * Report a problem that has no exception associated with it.
     * @param whileDoing what the app was going when the problem occurred,
     * in the contect "A problem occurred <whileDoing>"
     */
    public static void reportProblem(final String whileDoing) {
        reportProblem(whileDoing, null);
    }
    
    /**
     * Report a problem that has an exception associated with it.
     * @param whileDoing what the app was going when the problem occurred,
     * in the contect "A problem occurred <whileDoing>"
     * @param exception any Exception that occurred, or null if the
     * problem isn't due to an exception.
     */
    public static void reportProblem(final String whileDoing, final Exception exception) {
        final Thread callingThread = Thread.currentThread();
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                final ProblemDialog dialog = new ProblemDialog(parentFrame, whileDoing, exception, callingThread, pluginRegistry);
                dialog.pack();
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            }
        });
    }

    /**
     * Initialise the toolkit by obtaining the things it needs to create
     * Problem dialogs.
     * 
     * @param springLoader the spring loader
     */
    public static void initialise(final SpringLoader springLoader) {
        parentFrame = springLoader.getBean("mainFrame", Frame.class);
        pluginRegistry = springLoader.getBean("pluginRegistry", PluginRegistry.class);
    }
}

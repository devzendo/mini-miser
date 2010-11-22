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

package org.devzendo.minimiser.gui.dialog.welcome;

import java.awt.Frame;

import javax.swing.SwingUtilities;

import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;


/**
 * Toolkit for easily creating Welcome Dialogs.
 * 
 * @author matt
 *
 */
public final class WelcomeDialogHelper {
    private static Frame mParentFrame;
    private static CursorManager mCursorManager;
    private static PluginRegistry mPluginRegistry;

    /**
     * 
     */
    private WelcomeDialogHelper() {
        // no instances
    }
    
    /**
     * Initialise the toolkit by obtaining the things it needs to create
     * Welcome dialogs.
     * 
     * @param springLoader the spring loader
     */
    public static void initialise(final SpringLoader springLoader) {
        mParentFrame = springLoader.getBean("mainFrame", Frame.class);
        mCursorManager = springLoader.getBean("cursorManager", CursorManager.class);
        mPluginRegistry = springLoader.getBean("pluginRegistry", PluginRegistry.class);
        assert mParentFrame != null;
        assert mCursorManager != null;
        assert mPluginRegistry != null;
    }
    
    /**
     * Show the welcome dialog
     */
    public static void showWelcomeDialog() {
        showDialog(true);
    }
    
    /**
     * Show the what's new dialog 
     */
    public static void showWhatsNewDialog() {
        showDialog(false);
    }
    
    private static void showDialog(final boolean isWelcome) {
        assert SwingUtilities.isEventDispatchThread();
        final WelcomeDialog dialog = new WelcomeDialog(mParentFrame,
            mCursorManager, mPluginRegistry, isWelcome);
        dialog.postConstruct();
        dialog.pack();
        dialog.setLocationRelativeTo(mParentFrame);
        dialog.setVisible(true);
    }
}

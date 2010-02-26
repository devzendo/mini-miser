package org.devzendo.minimiser.gui.dialog.welcome;

import java.awt.Frame;

import javax.swing.SwingUtilities;

import org.devzendo.minimiser.gui.CursorManager;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.springloader.SpringLoader;


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
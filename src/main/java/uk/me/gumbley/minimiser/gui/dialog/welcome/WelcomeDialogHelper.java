package uk.me.gumbley.minimiser.gui.dialog.welcome;

import java.awt.Frame;

import javax.swing.SwingUtilities;

import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * Toolkit for easily creating Welcome Dialogs.
 * 
 * @author matt
 *
 */
public final class WelcomeDialogHelper {
    private static Frame mParentFrame;
    private static CursorManager mCursorManager;
    private static AppDetails mAppDetails;

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
        mAppDetails = springLoader.getBean("appDetails", AppDetails.class);
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
            mCursorManager, mAppDetails, isWelcome);
        dialog.pack();
        dialog.setLocationRelativeTo(mParentFrame);
        dialog.setVisible(true);
    }
}

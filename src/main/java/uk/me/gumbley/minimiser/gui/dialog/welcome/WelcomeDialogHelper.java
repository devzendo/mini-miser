package uk.me.gumbley.minimiser.gui.dialog.welcome;

import java.awt.Frame;
import javax.swing.SwingUtilities;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * Toolkit for easily creating Welcome Dialogs.
 * 
 * @author matt
 *
 */
public final class WelcomeDialogHelper {
    private static Frame parentFrame;
    private static CursorManager cursorManager;

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
        parentFrame = springLoader.getBean("mainFrame", Frame.class);
        cursorManager = springLoader.getBean("cursorManager", CursorManager.class);
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
        final WelcomeDialog dialog = new WelcomeDialog(parentFrame, cursorManager, isWelcome);
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }
}

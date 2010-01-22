package org.devzendo.minimiser.gui.dialog.problem;

import java.awt.Frame;

import org.devzendo.commoncode.gui.GUIUtils;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.springloader.SpringLoader;


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

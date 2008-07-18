package uk.me.gumbley.minimiser;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.exception.AppException;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.gui.ThreadCheckingRepaintManager;
import uk.me.gumbley.commoncode.logging.Logging;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.Beautifier;
import uk.me.gumbley.minimiser.gui.MainFrame;
import uk.me.gumbley.minimiser.gui.wizard.MiniMiserWizardPage;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.DefaultPrefsImpl;
import uk.me.gumbley.minimiser.prefs.PrefsFactory;
import uk.me.gumbley.minimiser.prefs.PrefsLocation;
import uk.me.gumbley.minimiser.springloader.SpringLoader;
import uk.me.gumbley.minimiser.springloader.SpringLoaderFactory;
import uk.me.gumbley.minimiser.version.AppVersion;

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
            LOGGER.info(String.format("Prefs directory %s does not exist - creating it", prefsLocation.getPrefsDir().getAbsolutePath()));
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
            String.format("%s could not create the '%s' folder, and cannot continue.\n"
                + "This folder would be used to remember your options and settings.\n\n"
                + "Failure to create this folder may be be due to security permissions, or a full disk.",
                AppName.getAppName(),
                prefsLocation.getPrefsDir().getAbsolutePath()),
            "Could not create settings folder",
            JOptionPane.ERROR_MESSAGE);
        
        System.exit(0);
    }

    /**
     * @param args the command line args
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure();
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        argList = Logging.getInstance().setupLoggingFromArgs(argList);
        LOGGER.info(String.format("%s %s starting...", AppName.getAppName(), AppVersion.getVersion()));
        final ArrayList<String> finalArgList = argList;
        final SpringLoader springLoader = initSpringLoader();
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
                    new MainFrame(springLoader, finalArgList);
                    // TODO perhaps make this wait for the main window to
                    // appear first, i.e. as an AWTEventListener
                    triggerGUIStartupTasks(springLoader);
                } catch (final AppException e) {
                    LOGGER.fatal(e.getMessage());
                    System.exit(1);
                }
            }

        });
    }
    
    private static void triggerGUIStartupTasks(final SpringLoader springLoader) {
        MiniMiserWizardPage.setLHGraphic();
        final Prefs prefs = springLoader.getBean("prefs", DefaultPrefsImpl.class);
        MiniMiserWizardPage.getPanelDimension(prefs);
    }

    private static SpringLoader initSpringLoader() {
        // Now load up Spring...
        final long startSpring = System.currentTimeMillis();
        final SpringLoader sl = SpringLoaderFactory
                .initialise("uk/me/gumbley/minimiser/MiniMiser.xml");
        final long stopSpring = System.currentTimeMillis();
        final long springElapsed = stopSpring - startSpring;
        LOGGER.debug("SpringLoader initialised in "
                + StringUtils.translateTimeDuration(springElapsed));
        return sl;
    }
}

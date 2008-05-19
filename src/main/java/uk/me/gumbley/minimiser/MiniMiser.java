package uk.me.gumbley.minimiser;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.exception.AppException;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.logging.Logging;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.MainFrame;
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

    /**
     * @param args the command line args
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure();
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        argList = Logging.getInstance().setupLoggingFromArgs(argList);
        LOGGER.info(String.format("%s %s starting...", AppName.getAppName(), AppVersion.getVersion()));
        final ArrayList<String> finalArgList = argList;
        final SpringLoader sl = initSpringLoader();
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
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                try {
                    new MainFrame(sl, finalArgList);
                } catch (AppException e) {
                    LOGGER.fatal(e.getMessage());
                    System.exit(1);
                }
            }
        });
    }

    private static SpringLoader initSpringLoader() {
        // Now load up Spring...
        long startSpring = System.currentTimeMillis();
        SpringLoader sl = SpringLoaderFactory
                .initialise("uk/me/gumbley/minimiser/MiniMiser.xml");
        long stopSpring = System.currentTimeMillis();
        long springElapsed = stopSpring - startSpring;
        LOGGER.debug("SpringLoader initialised in "
                + StringUtils.translateTimeDuration(springElapsed));
        return sl;
    }
}

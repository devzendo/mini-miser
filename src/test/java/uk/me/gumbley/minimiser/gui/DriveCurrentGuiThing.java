package uk.me.gumbley.minimiser.gui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.logging.Logging;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.dialog.PasswordEntryDialogHelper;
import uk.me.gumbley.minimiser.gui.dialog.ProblemDialog;
import uk.me.gumbley.minimiser.version.AppVersion;


/**
 * @author matt
 *
 */
public final class DriveCurrentGuiThing {
    private static final Logger LOGGER = Logger
            .getLogger(DriveCurrentGuiThing.class);
    private DriveCurrentGuiThing() {
        // nop
    }
    /**
     * @param args command line
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure();
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        argList = Logging.getInstance().setupLoggingFromArgs(argList);
        LOGGER.info(String.format("%s %s GUI experimentation starting...", AppName.getAppName(), AppVersion.getVersion()));
        Beautifier.makeBeautiful();
        
        final JFrame frame = new JFrame("title");
        final JButton button = new JButton("hello");
        button.setPreferredSize(new Dimension(400, 200));
        frame.add(button);
        frame.pack();
        frame.setVisible(true);

        //enterPassword(frame);
        //problemDialogNoException(frame);
        problemDialogException(frame);
    }
    private static void problemDialogException(JFrame frame) {
        ProblemDialog.reportProblem(frame, "working out the meaning of life", new RuntimeException("the obstacle is the path"));;
    }
    private static void problemDialogNoException(JFrame frame) {
        ProblemDialog.reportProblem(frame, "working out the meaning of life");
    }
    private static void enterPassword(final JFrame frame) {
        new PasswordEntryDialogHelper().promptForPassword(frame, "foo");
    }
}

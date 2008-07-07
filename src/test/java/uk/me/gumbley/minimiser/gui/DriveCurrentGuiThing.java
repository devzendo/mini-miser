package uk.me.gumbley.minimiser.gui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.logging.Logging;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.dialog.PasswordEntryDialog;
import uk.me.gumbley.minimiser.gui.dialog.SeriousProblemDialog;
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
        
        JFrame frame = new JFrame("title");
        final JButton button = new JButton("hello");
        button.setPreferredSize(new Dimension(400, 200));
        frame.add(button);
        frame.pack();
        frame.setVisible(true);
        
        SeriousProblemDialog dialog = new SeriousProblemDialog(frame, "doing something innoccuous", new Exception("Could not find will to unit test GUI components"));
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
}

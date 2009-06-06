package uk.me.gumbley.minimiser.gui.dialog.dstamessage;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.logging.Logging;
import uk.me.gumbley.minimiser.gui.Beautifier;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.TestPrefs;


/**
 * @author matt
 *
 */
public final class DriveDSTAMessage {

    private static final Logger LOGGER = Logger
            .getLogger(DriveDSTAMessage.class);
    
    private final Prefs prefs;
    private final File prefsFile;
    private final DSTAMessageFactory messageFactory;
    
    private DriveDSTAMessage() throws IOException {
        final JFrame frame = new JFrame("title");
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        final JButton trigger = new JButton("Message");
        trigger.addActionListener(new ActionListener () {
            public void actionPerformed(final ActionEvent e) {
                showMessage();
            }
        });
        
        frame.add(trigger);
        
        final JButton clear = new JButton("Clear DSTA Flags");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                prefs.clearAllDontShowThisAgainFlags();
            }
        });
        
        frame.add(clear);
        
        frame.pack();
        frame.setVisible(true);
        
        prefs = TestPrefs.createUnitTestPrefsFile();
        prefsFile = new File(prefs.getAbsolutePath());
        LOGGER.info("Prefs file is at " + prefsFile.getAbsolutePath());
        prefsFile.deleteOnExit();
        
        messageFactory = new DefaultDSTAMessageFactoryImpl(prefs, frame);
    }

    /**
     * 
     */
    protected void showMessage() {
        messageFactory.possiblyShowMessage(DSTAMessageId.TEST,
            "Here is a component containing text.\nThere are several lines...\n\nAnd they can get quite wide also, which is nice.");
    }

    /**
     * @param args command line
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure();
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        argList = Logging.getInstance().setupLoggingFromArgs(argList);
        

        GUIUtils.runOnEventThread(new Runnable() {

            public void run() {
                LOGGER.info("SQL Tab experimentation starting...");
                Beautifier.makeBeautiful();
                try {
                    new DriveDSTAMessage();
                } catch (final IOException e) {
                    LOGGER.warn("Couldn't create test prefs: " + e.getMessage(), e);
                }
            }
        });
    }
}

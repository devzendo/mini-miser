package uk.me.gumbley.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.logging.Logging;
import uk.me.gumbley.minimiser.gui.dialog.welcome.WelcomeDialog;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;


/**
 * @author matt
 *
 */
public final class DriveWelcome {
    private DriveWelcome() {
        // nop
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
                Beautifier.makeBeautiful(new AppDetails());
                
                final JFrame frame = new JFrame("title");
                frame.setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setPreferredSize(new Dimension(800, 600));
                
                final CursorManager cursorManager = new CursorManager();
                cursorManager.setMainFrame(frame);
                
                final JButton launch = new JButton("Launch Welcome");
                launch.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        final AppDetails appDetails = new AppDetails();
                        appDetails.setApplicationName("DriveWelcome");
                        appDetails.setApplicationVersion("0.0.0");
                        final WelcomeDialog welcome = new WelcomeDialog(frame, cursorManager, appDetails, true);
                        welcome.pack();
                        welcome.setVisible(true);
                    }
                });
                frame.add(launch, BorderLayout.NORTH);
                frame.pack();
                frame.setVisible(true);
            }
            
        });
    }
}

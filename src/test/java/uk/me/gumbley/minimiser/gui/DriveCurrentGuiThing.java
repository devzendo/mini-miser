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
import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.commoncode.logging.Logging;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.dialog.PasswordEntryDialogHelper;
import uk.me.gumbley.minimiser.gui.dialog.ProblemDialog;
import uk.me.gumbley.minimiser.util.DelayedExecutor;
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
        frame.setLayout(new BorderLayout());

        final MainFrameStatusBar sb = new MainFrameStatusBar(new DelayedExecutor());
        frame.add(sb.getPanel(), BorderLayout.SOUTH);

        
        final JButton button = new JButton("hello");
        button.setPreferredSize(new Dimension(400, 200));
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        sb.displayMessage("test this one!");
                        ThreadUtils.waitNoInterruption(500);
                        sb.clearMessage();
                        ThreadUtils.waitNoInterruption(500);
                        sb.displayMessage("permanent message");
                        ThreadUtils.waitNoInterruption(500);
                        sb.displayTemporaryMessage("temp message", 2);
                        ThreadUtils.waitNoInterruption(5000);
                        sb.setProgressLength(10);
                        for (int i=0; i<10; i++) {
                            sb.setProgressStep(i);
                            ThreadUtils.waitNoInterruption(500);
                        }
                        sb.clearProgress();
                        sb.clearMessage();
                    }}).start();
            }});
        frame.add(button, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);

//        enterPassword(frame);
//        problemDialogNoException(frame);
//        problemDialogException(frame);
        
        
    }
    private static void problemDialogException(final JFrame frame) {
        ProblemDialog.reportProblem(frame, "working out the meaning of life", new RuntimeException("the obstacle is the path"));
    }
    private static void problemDialogNoException(final JFrame frame) {
        ProblemDialog.reportProblem(frame, "working out the meaning of life");
    }
    private static void enterPassword(final JFrame frame) {
        new PasswordEntryDialogHelper().promptForPassword(frame, "foo");
    }
}

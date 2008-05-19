package uk.me.gumbley.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.commoncode.exception.AppException;
import uk.me.gumbley.commoncode.gui.ThreadCheckingRepaintManager;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.springloader.SpringLoader;
import uk.me.gumbley.minimiser.version.AppVersion;

/**
 * The MiniMiser main Frame - this is the main application start code.
 * 
 * @author matt
 */
public class MainFrame {
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class);

    private JFrame mainFrame;

    private ActionListener exitAL;

    @SuppressWarnings("unused")
    private final SpringLoader springLoader;

    /**
     * @param loader the IoC container abstraction
     * @param argList the command line arguments after logging has been processed
     * @throws AppException upon fatal application failure
     */
    public MainFrame(final SpringLoader loader, 
            final ArrayList<String> argList)
            throws AppException {
        super();
        this.springLoader = loader;
        // Process command line
        for (int i = 0; i < argList.size(); i++) {
            LOGGER.debug("arg " + i + " = '" + argList.get(i) + "'");
        }
        ThreadCheckingRepaintManager.initialise();
        // Create new Window and exit handler
        createMainFrame();
        // Menu
        mainFrame.add(createMenu(), BorderLayout.NORTH);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private void createMainFrame() {
        mainFrame = new JFrame(AppName.getAppName() + " v"
                + AppVersion.getVersion());
        mainFrame.setLayout(new BorderLayout());
        exitAL = new WindowCloseActionListener(mainFrame, new MainFrameFacade() {
            public void enableDisableControls(final boolean enable) {
                MainFrame.this.enableDisableControls(enable);
            }

            public void shutdown() {
                MainFrame.this.shutdown();
            }
        });
        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowListener() {
            public void windowOpened(final WindowEvent e) {
            }

            public void windowClosing(final WindowEvent e) {
                exitAL.actionPerformed(null);
            }

            public void windowClosed(final WindowEvent e) {
            }

            public void windowIconified(final WindowEvent e) {
            }

            public void windowDeiconified(final WindowEvent e) {
            }

            public void windowActivated(final WindowEvent e) {
            }

            public void windowDeactivated(final WindowEvent e) {
            }
        });
    }

    private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        //
        //
        JMenuItem fileExit = new JMenuItem("Exit");
        fileExit.setMnemonic('x');
        fileExit.addActionListener(exitAL);
        fileMenu.add(fileExit);
        //
        // ---
        //
        JMenu helpMenu = new JMenu("Help");
        //
        JMenuItem helpManual = new JMenuItem("Help contents");
        helpMenu.add(helpManual);
        //
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void shutdown() {
        long start = System.currentTimeMillis();
        long stop = System.currentTimeMillis();
        long dur = stop - start;
        LOGGER.info("Shutdown took " + StringUtils.translateTimeDuration(dur));
        // give time for the above to be logged
        ThreadUtils.waitNoInterruption(500);
        if (mainFrame != null) {
            mainFrame.dispose();
        }
        System.exit(0);
    }

    private void enableDisableControls(final boolean enable) {
    }
}

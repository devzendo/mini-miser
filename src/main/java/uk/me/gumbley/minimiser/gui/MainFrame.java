package uk.me.gumbley.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.commoncode.exception.AppException;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.mm.Menu;
import uk.me.gumbley.minimiser.gui.mm.MenuBuilder;
import uk.me.gumbley.minimiser.gui.mm.MenuMediator;
import uk.me.gumbley.minimiser.gui.mm.Menu.MenuIdentifier;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.springloader.SpringLoader;
import uk.me.gumbley.minimiser.util.DelayedExecutor;
import uk.me.gumbley.minimiser.version.AppVersion;

/**
 * The MiniMiser main Frame - this is the main application start code.
 * 
 * @author matt
 */
public class MainFrame {
    private static final String MAINFRAME_WINDOW_NAME = "mainframe";

    private static final Logger LOGGER = Logger.getLogger(MainFrame.class);

    private JFrame mainFrame;
    private ActionListener exitAL;
    private final SpringLoader springLoader;
    private final DelayedExecutor delayedExecutor;
    private final Prefs prefs;
    private final CursorManager cursorManager;
    private final WindowGeometryStore windowGeometryStore;

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
        delayedExecutor = springLoader.getBean("delayedExecutor", DelayedExecutor.class);
        prefs = springLoader.getBean("prefs", Prefs.class);
        windowGeometryStore = springLoader.getBean("windowGeometryStore", WindowGeometryStore.class);
        windowGeometryStore.startListener();

        // Create new Window and exit handler
        createMainFrame();
        cursorManager = springLoader.getBean("cursorManager", CursorManager.class);
        cursorManager.setMainFrame(mainFrame);
        
        // Menu
        mainFrame.add(createMenu(), BorderLayout.NORTH);
        mainFrame.add(createBlankPanel(), BorderLayout.CENTER);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private Component createBlankPanel() {
        JPanel blankPanel = new JPanel();
        blankPanel.setPreferredSize(new Dimension(640, 480));
        return blankPanel;
    }

    private void createMainFrame() {
        mainFrame = new JFrame(AppName.getAppName() + " v"
                + AppVersion.getVersion());
        windowGeometryStore.watchWindow(mainFrame);
        //setStartingGeometry();
        
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
//        mainFrame.addComponentListener(new ComponentListener() {
//            public void componentHidden(final ComponentEvent e) {
//            }
//
//            public void componentMoved(final ComponentEvent e) {
//                saveGeometry(mainFrame.getBounds());
//            }
//
//            public void componentResized(final ComponentEvent e) {
//                saveGeometry(mainFrame.getBounds());
//            }
//
//            public void componentShown(final ComponentEvent e) {
//            }
//        });
    }

    private void setStartingGeometry() {
        try {
            final String geomStr = prefs.getWindowGeometry(MAINFRAME_WINDOW_NAME);
            LOGGER.debug("Starting geometry is " + geomStr);
            // x,y,width,height
            final String[] geomNumStrs = geomStr.split(",");
            final int[] geomNums = new int[geomNumStrs.length];
            for (int i = 0; i < geomNumStrs.length; i++) {
                geomNums[i] = Integer.parseInt(geomNumStrs[i]);
            }
            mainFrame.setLocation(geomNums[0], geomNums[1]);
            mainFrame.setPreferredSize(new Dimension(geomNums[2], geomNums[3]));
            LOGGER.debug("Starting geometry set");
        } catch (final NumberFormatException nfe) {
            LOGGER.warn("Couldn't set starting geometry", nfe);
        }
    }

    private void saveGeometry(final Rectangle rect) {
        final String geomStr = String.format("%d,%d,%d,%d",
                    rect.x, rect.y, rect.width, rect.height);
        delayedExecutor.submit("savegeometry", 250L, new Runnable() {
            public void run() {
                prefs.setWindowGeometry(MAINFRAME_WINDOW_NAME, geomStr);
            }
        });
        LOGGER.info(geomStr);
    }
    
    private JMenuBar createMenu() {
        LOGGER.info("Getting the menu");
        final Menu menu = springLoader.getBean("menu", Menu.class);
        LOGGER.info("Got the menu");
        menu.addMenuActionListener(MenuIdentifier.FileExit, exitAL);
        // wire up dependencies
        springLoader.getBean("menuMediator", MenuMediator.class);
        LOGGER.info("Menu dependencies wired");
        springLoader.getBean("menuBuilder", MenuBuilder.class).build();
        LOGGER.info("Menu ActionListeners built and wired");
        return menu.getMenuBar();
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

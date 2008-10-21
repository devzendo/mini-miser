package uk.me.gumbley.minimiser.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.exception.AppException;
import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.gui.menu.MenuBuilder;
import uk.me.gumbley.minimiser.gui.menu.MenuMediator;
import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;
import uk.me.gumbley.minimiser.gui.tabpanemanager.TabPaneManager;
import uk.me.gumbley.minimiser.lifecycle.LifecycleManager;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.recentlist.RecentFilesList;
import uk.me.gumbley.minimiser.springloader.SpringLoader;
import uk.me.gumbley.minimiser.version.AppVersion;

/**
 * The MiniMiser main Frame - this is the main application start code.
 * 
 * @author matt
 */
public class MainFrame {
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class);
    private static final String MAIN_FRAME_NAME = "main";

    private JFrame mainFrame;
    private ActionListener exitAL;
    private final SpringLoader springLoader;
    private final CursorManager cursorManager;
    private final WindowGeometryStore windowGeometryStore;
    private final OpenDatabaseList openDatabaseList;
    private final RecentFilesList recentList;
    private final MainFrameStatusBar statusBar;
    private LifecycleManager lifecycleManager;

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

        windowGeometryStore = springLoader.getBean("windowGeometryStore", WindowGeometryStore.class);
        openDatabaseList = springLoader.getBean("openDatabaseList", OpenDatabaseList.class);
        recentList = springLoader.getBean("recentFilesList", RecentFilesList.class);

        // Create new Window and exit handler
        createMainFrame();
        cursorManager = springLoader.getBean("cursorManager", CursorManager.class);
        cursorManager.setMainFrame(mainFrame);
        
        statusBar = springLoader.getBean("statusBar", MainFrameStatusBar.class);
        mainFrame.add(statusBar.getPanel(), BorderLayout.SOUTH);
        
        // Menu
        mainFrame.add(createMenu(), BorderLayout.NORTH);
        
        // Main panel
        final TabPaneManager tabPaneManager = springLoader.getBean("tabPaneManager", TabPaneManager.class);
        final JPanel mainPanel = tabPaneManager.getMainPanel();
        mainPanel.setPreferredSize(new Dimension(640, 480));
        mainFrame.add(mainPanel, BorderLayout.CENTER);
        
        if (!windowGeometryStore.hasStoredGeometry(mainFrame)) {
            mainFrame.pack();
        }
        windowGeometryStore.loadGeometry(mainFrame);
        attachVisibilityListenerForLifecycleManagerStartup();
        mainFrame.setVisible(true);
    }

    private void attachVisibilityListenerForLifecycleManagerStartup() {
        final AWTEventListener awtEventListener = new AWTEventListener() {
            public void eventDispatched(final AWTEvent event) {
                if (event.getID() == WindowEvent.WINDOW_OPENED && event.getSource().equals(mainFrame)) {
                    LOGGER.info("Main frame visible; starting lifecycle manager");
                    cursorManager.hourglass(this.getClass().getSimpleName());
                    final SwingWorker worker = new SwingWorker() {
                        @Override
                        public Object construct() {
                            Thread.currentThread().setName("Lifecycle Startup");
                            lifecycleManager = springLoader.getBean("lifecycleManager", LifecycleManager.class);
                            lifecycleManager.startup();
                            return null;
                        }
                        public void finished() {
                            cursorManager.normal(this.getClass().getSimpleName());
                        }
                    };
                    worker.start();
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(awtEventListener, AWTEvent.WINDOW_EVENT_MASK);
    }

    private void createMainFrame() {
        mainFrame = new JFrame(AppName.getAppName() + " v"
                + AppVersion.getVersion());

        mainFrame.setIconImage(createImageIcon("icons/application.gif").getImage());

        setMainFrameInFactory();
        
        createAndSetMainFrameTitleInFactory();

        mainFrame.setName(MAIN_FRAME_NAME);
        mainFrame.setLayout(new BorderLayout());
        
        exitAL = new WindowCloseActionListener();

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

    /**
     *  Returns an ImageIcon, or null if the path was invalid.
     */
    private ImageIcon createImageIcon(final String path) {
        final java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            LOGGER.warn("Couldn't find file: " + path);
            return null;
        }
    }
    private void createAndSetMainFrameTitleInFactory() {
        final MainFrameTitleFactory mainFrameTitleFactory = springLoader.getBean("&mainFrameTitle", MainFrameTitleFactory.class);
        mainFrameTitleFactory.setMainFrameTitle(new DefaultMainFrameTitleImpl(mainFrame));
    }

    private void setMainFrameInFactory() {
        final MainFrameFactory mainFrameFactory = springLoader.getBean("&mainFrame", MainFrameFactory.class);
        mainFrameFactory.setMainFrame(mainFrame);
    }
    
    private final class WindowCloseActionListener implements ActionListener {
        /**
         * Note that e can be null when called from the adapted WindowListener.
         * {@inheritDoc}
         * EDT
         */
        public void actionPerformed(final ActionEvent e) {
            // only ask if there are databases open
            final boolean anyDatabasesOpen = openDatabaseList.getNumberOfDatabases() > 0;
            if (anyDatabasesOpen) {
                if (askExitSaysYes()) {
                    doShutdown();
                }
            } else {
                doShutdown();
            }
        }

        // EDT
        private boolean askExitSaysYes() {
            final int opt = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to exit?", "Confirm exit",
                JOptionPane.YES_NO_OPTION);
            return (opt == 0);
        }
        
        // EDT
        private void doShutdown() {
            cursorManager.hourglass(this.getClass().getSimpleName());
            enableDisableControls(false);
            
            final SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    Thread.currentThread().setName("Lifecycle Shutdown");
                    LOGGER.info("Shutting down...");
                    if (lifecycleManager != null) {
                        lifecycleManager.shutdown();
                    }
                    return null;
                }
                
                public void finished() {
                    if (mainFrame != null) {
                        mainFrame.dispose();
                    }
                    cursorManager.normal(this.getClass().getSimpleName());
                    System.exit(0);
                }
            };
            worker.start();
        }
    }
    
    private JMenuBar createMenu() {
        LOGGER.info("Getting the menu");
        final Menu menu = springLoader.getBean("menu", Menu.class);
        menu.refreshRecentList(recentList.getRecentDatabases());
        // TODO SMELL why not have it populated correctly at first?
        // we could pass in the recent strings to a ctor?
        // NOTE the view menu is initially populated via a lifecycle
        LOGGER.info("Got the menu");
        menu.addMenuActionListener(MenuIdentifier.FileExit, exitAL);
        // wire up dependencies
        springLoader.getBean("menuMediator", MenuMediator.class);
        LOGGER.info("Menu dependencies wired");
        springLoader.getBean("menuBuilder", MenuBuilder.class).build();
        LOGGER.info("Menu ActionListeners built and wired");
        return menu.getMenuBar();
    }

    private void enableDisableControls(final boolean enable) {
        // TODO
    }
}

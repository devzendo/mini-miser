package uk.me.gumbley.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.commoncode.exception.AppException;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.dialog.ProblemDialog;
import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.gui.menu.MenuBuilder;
import uk.me.gumbley.minimiser.gui.menu.MenuMediator;
import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;
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
        windowGeometryStore = springLoader.getBean("windowGeometryStore", WindowGeometryStore.class);
        openDatabaseList = springLoader.getBean("openDatabaseList", OpenDatabaseList.class);
        recentList = springLoader.getBean("recentFilesList", RecentFilesList.class);

        // Create new Window and exit handler
        createMainFrame();
        cursorManager = springLoader.getBean("cursorManager", CursorManager.class);
        cursorManager.setMainFrame(mainFrame);
        
        // Menu
        mainFrame.add(createMenu(), BorderLayout.NORTH);
        mainFrame.add(createBlankPanel(), BorderLayout.CENTER);
        if (!windowGeometryStore.hasStoredGeometry(mainFrame)) {
            mainFrame.pack();
        }
        windowGeometryStore.loadGeometry(mainFrame);
        mainFrame.setVisible(true);
    }

    private Component createBlankPanel() {
        final JPanel blankPanel = new JPanel();
        blankPanel.setPreferredSize(new Dimension(640, 480));
        return blankPanel;
    }

    private void createMainFrame() {
        mainFrame = new JFrame(AppName.getAppName() + " v"
                + AppVersion.getVersion());
        mainFrame.setName(MAIN_FRAME_NAME);
        
        mainFrame.setLayout(new BorderLayout());
        exitAL = new WindowCloseActionListener(mainFrame, new MainFrameFacade() {
            public void enableDisableControls(final boolean enable) {
                MainFrame.this.enableDisableControls(enable);
            }

            public void shutdown() {
                // TODO flush DelayedExecutor thread
                closeOpenDatabases();
                windowGeometryStore.saveGeometry(mainFrame);
                MainFrame.this.shutdown();
            }
            
            public boolean anyDatabasesOpen() {
                return openDatabaseList.getNumberOfDatabases() > 0;
            }
            
            private void closeOpenDatabases() {
                final List<DatabaseDescriptor> openDatabases = openDatabaseList.getOpenDatabases();
                for (DatabaseDescriptor descriptor : openDatabases) {
                    // TODO perhaps DatabaseDescriptor should be polymorphic?
                    final String databaseName = descriptor.getDatabaseName();
                    if (descriptor instanceof MiniMiserDatabaseDescriptor) {
                        final MiniMiserDatabaseDescriptor mmdd = (MiniMiserDatabaseDescriptor) descriptor;
                        try {
                            LOGGER.info("Closing database '" + databaseName + "'");
                            mmdd.getDatabase().close();
                            LOGGER.info("Database '" + databaseName + "' closed");
                        } catch (final DataAccessException dae) {
                            LOGGER.warn("Could not close database '" + databaseName + "': " + dae.getMessage(), dae);
                            // TODO pass main frame in here
                            ProblemDialog.reportProblem(null, "while closing the '" + databaseName + "' database", dae);
                        }
                    } else {
                        LOGGER.error("Closing a non-MiniMiserDatabaseDescriptor");
                    }
                }
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
        LOGGER.info("Getting the menu");
        final Menu menu = springLoader.getBean("menu", Menu.class);
        menu.refreshRecentList(recentList.getRecentFileNames());
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
        final long start = System.currentTimeMillis();
        final long stop = System.currentTimeMillis();
        final long dur = stop - start;
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

package uk.me.gumbley.minimiser.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.exception.AppException;
import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.commoncode.resource.ResourceLoader;
import uk.me.gumbley.minimiser.gui.tab.impl.sql.SQLTabPanel;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;
import uk.me.gumbley.minimiser.springloader.SpringLoader;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * A stand-alone console frame that opens a database without
 * using the Opener. Ideal for fixing migration failures.
 * 
 * @author matt
 *
 */
public final class SqlConsoleFrame {
    private static final Logger LOGGER = Logger
            .getLogger(SqlConsoleFrame.class);
    private static final String MAIN_FRAME_NAME = "main";
    private JFrame mMainFrame;
    private final SpringLoader mSpringLoader;
    private final CursorManager mCursorManager;
    private final WindowGeometryStore mWindowGeometryStore;
    private final JPanel mBlankPanel;
    private String dbPath = "";
    private String dbPassword = "";
    private AccessFactory mAccessFactory;
    private InstanceSet<DAOFactory> mOpenDatabase;
    private PluginRegistry mPluginRegistry;
    private String dbName;
    private DatabaseDescriptor mDatabaseDescriptor;

    /**
     * @param springLoader the IoC container abstraction
     * @param argList the command line arguments after logging has been processed
     * @throws AppException upon fatal application failure
     */
    public SqlConsoleFrame(final SpringLoader springLoader, 
            final ArrayList<String> argList)
            throws AppException {
        super();
        mSpringLoader = springLoader;

        validateArgs(argList);
        mWindowGeometryStore = mSpringLoader.getBean("windowGeometryStore", WindowGeometryStore.class);

        // Create new Window and exit handler
        createMainFrame();
        mCursorManager = mSpringLoader.getBean("cursorManager", CursorManager.class);
        mCursorManager.setMainFrame(mMainFrame);

        mBlankPanel = new JPanel();
        mBlankPanel.setPreferredSize(new Dimension(640, 480));
        mMainFrame.add(mBlankPanel, BorderLayout.CENTER);
        
        if (!mWindowGeometryStore.hasStoredGeometry(mMainFrame)) {
            mMainFrame.pack();
        }
        mWindowGeometryStore.loadGeometry(mMainFrame);
        attachVisibilityListenerForLifecycleManagerStartup();
        mMainFrame.setVisible(true);
    }

    private void validateArgs(final ArrayList<String> argList) throws AppException {
        final int argLen = argList.size();
        dbPath = "";
        dbPassword = "";
        for (int i = 0; i < argLen; i++) {
            final String arg = argList.get(i);
            if (arg.equals("-database")) {
                if (i < argLen - 1) {
                    dbPath = argList.get(++i);
                } else {
                    throw new AppException("Syntax error: -database <pathname>");
                }
            }
            if (arg.equals("-password")) {
                if (i < argLen - 1) {
                    dbPassword = argList.get(++i);
                } else {
                    throw new AppException("Syntax error: -password <password>");
                }
            }
        }
        if (dbPath.equals("")) {
            throw new AppException("Error: no path to the database supplied");
        }
        final File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            throw new AppException("Error: Database path '" + dbPath + "' does not exist");
        }
        dbName = dbFile.getName();
        dbPath = dbPath + File.separatorChar + dbName;
    }

    private void attachVisibilityListenerForLifecycleManagerStartup() {
        Toolkit.getDefaultToolkit().addAWTEventListener(
            new AWTEventListener() {

                public void eventDispatched(final AWTEvent event) {
                    if (event.getID() == WindowEvent.WINDOW_OPENED && event.getSource().equals(mMainFrame)) {
                        mCursorManager.hourglass(this.getClass().getSimpleName());
                        final SwingWorker worker = new SwingWorker() {
                            private AppException mFatalException;
                            @Override
                            public Object construct() {
                                Thread.currentThread().setName("Database Opener");
                                LOGGER.info("Main frame visible; opening console");
                                try {
                                    openDatabase();
                                } catch (final AppException e) {
                                    LOGGER.fatal(e.getMessage(), e);
                                    mFatalException = e;
                                }
                                return null;
                            }
                            @Override
                            public void finished() {
                                mCursorManager.normal(this.getClass().getSimpleName());
                                LOGGER.info("Main frame visible; opening console");
                                if (mDatabaseDescriptor != null) {
                                    final SQLTabPanel sqlTabPanel =
                                        new SQLTabPanel(
                                            mDatabaseDescriptor,
                                            mCursorManager,
                                            mPluginRegistry);
                                    mMainFrame.remove(mBlankPanel);
                                    sqlTabPanel.setPreferredSize(new Dimension(640, 480));
                                    mMainFrame.add(sqlTabPanel, BorderLayout.CENTER);
                                    mMainFrame.addWindowListener(new WindowAdapter() {
                                        @Override
                                        public void windowClosing(final WindowEvent e) {
                                            LOGGER.info("Closing database");
                                            mOpenDatabase.getInstanceOf(MiniMiserDAOFactory.class).close();
                                            LOGGER.info("Closed");
                                            mMainFrame.dispose();
                                            System.exit(0);
                                        }
                                    });
                                } else {
                                    JOptionPane.showMessageDialog(
                                        mMainFrame, 
                                        "Database '" + dbPath
                                            + "'\ncould not be opened:\n" 
                                            + mFatalException.getMessage(),
                                        "Could not open database",
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        };
                        worker.start();
                    }                    
                }
            },
            AWTEvent.WINDOW_EVENT_MASK);
    }

    private void openDatabase() throws AppException {
        mPluginRegistry = mSpringLoader.getBean("pluginRegistry", PluginRegistry.class);
        mAccessFactory = mSpringLoader.getBean("accessFactory", AccessFactory.class);
        try {
            mOpenDatabase = mAccessFactory.openDatabase(dbPath, dbPassword);
            mDatabaseDescriptor = new DatabaseDescriptor(dbName, dbPath);
            final MiniMiserDAOFactory miniMiserDaoFactory = 
                mOpenDatabase.getInstanceOf(MiniMiserDAOFactory.class);
            mDatabaseDescriptor.setDAOFactory(MiniMiserDAOFactory.class, miniMiserDaoFactory);
            mDatabaseDescriptor.setAttribute(AttributeIdentifier.Database, miniMiserDaoFactory);
        } catch (final RuntimeException dae) {
            throw new AppException("Could not open database at '" + dbPath + "': " + dae.getMessage(), dae);
        }
    }

    private void createMainFrame() {
        mMainFrame = new JFrame();

        mMainFrame.setIconImage(createImageIcon("icons/application.gif").getImage());
        mMainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        setMainFrameInFactory();
        
        createAndSetMainFrameTitleInFactory();

        mMainFrame.setName(MAIN_FRAME_NAME);
        mMainFrame.setLayout(new BorderLayout());
    }

    /**
     *  Returns an ImageIcon, or null if the path was invalid.
     */
    private ImageIcon createImageIcon(final String path) {
        final java.net.URL imgURL = ResourceLoader.getResourceURL(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            LOGGER.warn("Couldn't find file: " + path);
            return null;
        }
    }
    
    private void createAndSetMainFrameTitleInFactory() {
        final MainFrameTitleFactory mainFrameTitleFactory = mSpringLoader.getBean("&mainFrameTitle", MainFrameTitleFactory.class);
        mainFrameTitleFactory.setMainFrameTitle(
            new DefaultMainFrameTitleImpl(mMainFrame));
    }

    private void setMainFrameInFactory() {
        final MainFrameFactory mainFrameFactory = mSpringLoader.getBean("&mainFrame", MainFrameFactory.class);
        mainFrameFactory.setMainFrame(mMainFrame);
    }
}

package uk.me.gumbley.minimiser.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.exception.AppException;
import uk.me.gumbley.minimiser.gui.tabpanemanager.TabPaneManager;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * The MiniMiser main Frame - this is the main application start code.
 * 
 * @author matt
 */
public class MainFrame {
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class);
    private static final String MAIN_FRAME_NAME = "main";

    private JFrame mainFrame;
    private final SpringLoader springLoader;
    private final CursorManager cursorManager;
    private final WindowGeometryStore windowGeometryStore;
    private final MainFrameStatusBar statusBar;
    private final AppDetails mAppDetails;

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
        mAppDetails = springLoader.getBean("appDetails", AppDetails.class);
        windowGeometryStore = springLoader.getBean("windowGeometryStore", WindowGeometryStore.class);

        // Create new Window and exit handler
        createMainFrame();
        cursorManager = springLoader.getBean("cursorManager", CursorManager.class);
        cursorManager.setMainFrame(mainFrame);
        
        statusBar = springLoader.getBean("statusBar", MainFrameStatusBar.class);
        mainFrame.add(statusBar.getPanel(), BorderLayout.SOUTH);
        
        // Menu
        //mainFrame.setJMenuBar(createMenu());
        
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
        Toolkit.getDefaultToolkit().addAWTEventListener(
            new LifecycleStartupAWTEventListener(springLoader),
            AWTEvent.WINDOW_EVENT_MASK);
    }

    private void createMainFrame() {
        mainFrame = new JFrame(mAppDetails.getApplicationName() + " v"
                + mAppDetails.getApplicationVersion());

        mainFrame.setIconImage(createImageIcon("icons/application.gif").getImage());
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setMainFrameInFactory();
        
        createAndSetMainFrameTitleInFactory();

        mainFrame.setName(MAIN_FRAME_NAME);
        mainFrame.setLayout(new BorderLayout());
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
        mainFrameTitleFactory.setMainFrameTitle(
            new DefaultMainFrameTitleImpl(mainFrame, mAppDetails));
    }

    private void setMainFrameInFactory() {
        final MainFrameFactory mainFrameFactory = springLoader.getBean("&mainFrame", MainFrameFactory.class);
        mainFrameFactory.setMainFrame(mainFrame);
    }
}

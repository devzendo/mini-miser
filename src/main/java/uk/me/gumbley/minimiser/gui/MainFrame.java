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
import uk.me.gumbley.commoncode.resource.ResourceLoader;
import uk.me.gumbley.minimiser.gui.tabpanemanager.TabPaneManager;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * The MiniMiser main Frame - this is the main application start code.
 * 
 * @author matt
 */
public class MainFrame {
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class);
    private static final String MAIN_FRAME_NAME = "main";

    private JFrame mMainFrame;
    private final SpringLoader mSpringLoader;
    private final CursorManager mCursorManager;
    private final WindowGeometryStore mWindowGeometryStore;
    private final MainFrameStatusBar mStatusBar;
    private final PluginRegistry mPluginRegistry;

    /**
     * @param springLoader the IoC container abstraction
     * @param argList the command line arguments after logging has been processed
     * @throws AppException upon fatal application failure
     */
    public MainFrame(final SpringLoader springLoader, 
            final ArrayList<String> argList)
            throws AppException {
        super();
        mSpringLoader = springLoader;
        mPluginRegistry = mSpringLoader.getBean("pluginRegistry", PluginRegistry.class);
        mWindowGeometryStore = mSpringLoader.getBean("windowGeometryStore", WindowGeometryStore.class);

        // Create new Window and exit handler
        createMainFrame();
        mCursorManager = mSpringLoader.getBean("cursorManager", CursorManager.class);
        mCursorManager.setMainFrame(mMainFrame);
        
        mStatusBar = mSpringLoader.getBean("statusBar", MainFrameStatusBar.class);
        mMainFrame.add(mStatusBar.getPanel(), BorderLayout.SOUTH);
        
        // Menu
        //mainFrame.setJMenuBar(createMenu());
        
        // Main panel
        final TabPaneManager tabPaneManager = mSpringLoader.getBean("tabPaneManager", TabPaneManager.class);
        final JPanel mainPanel = tabPaneManager.getMainPanel();
        mainPanel.setPreferredSize(new Dimension(640, 480));
        mMainFrame.add(mainPanel, BorderLayout.CENTER);
        
        if (!mWindowGeometryStore.hasStoredGeometry(mMainFrame)) {
            mMainFrame.pack();
        }
        mWindowGeometryStore.loadGeometry(mMainFrame);
        attachVisibilityListenerForLifecycleManagerStartup();
        mMainFrame.setVisible(true);
    }

    private void attachVisibilityListenerForLifecycleManagerStartup() {
        Toolkit.getDefaultToolkit().addAWTEventListener(
            new LifecycleStartupAWTEventListener(mSpringLoader),
            AWTEvent.WINDOW_EVENT_MASK);
    }

    private void createMainFrame() {
        mMainFrame = new JFrame();

        mMainFrame.setIconImage(createImageIcon("icons/application.gif").getImage());
        mMainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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

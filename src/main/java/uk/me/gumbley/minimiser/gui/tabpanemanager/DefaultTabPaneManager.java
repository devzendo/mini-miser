package uk.me.gumbley.minimiser.gui.tabpanemanager;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * Extends the core Tab Pane Manager functionality with GUI code.
 * 
 * @author matt
 *
 */
public final class DefaultTabPaneManager extends AbstractTabPaneManager {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultTabPaneManager.class);
    private static final String BLANK_PANEL_NAME = "*special*blank*panel*";
    private static final String INTRO_PANEL_NAME = "*special*intro*panel*";
    
    private final JPanel mMainPanel;
    private final CardLayout mCardLayout;
    private JPanel mIntroPanel;
    private final SpringLoader mSpringLoader;
    
    /**
     * Construct the main panel with the card layout that'll accommodate the
     * tabbed panes.
     * @param springLoader used to load the intro panel when it
     * is first required. 
     * 
     * KLUDGE: Note that the intro panel is loaded delayed since
     * it needs the application plugin loading, and when the tab
     * manager is instantiated, the plugins may not be loaded.
     * When the tab panes are hidden, the plugins must have been
     * loaded, so the background graphic can be obtained.
     * 
     */
    public DefaultTabPaneManager(final SpringLoader springLoader) {
        super();
        mSpringLoader = springLoader;
        mIntroPanel = null;
        mMainPanel = new JPanel();
        mCardLayout = new CardLayout();
        mMainPanel.setLayout(mCardLayout);
        mMainPanel.add(new JPanel(), INTRO_PANEL_NAME);
        mMainPanel.add(new JPanel(), BLANK_PANEL_NAME);
        mCardLayout.show(mMainPanel, BLANK_PANEL_NAME);
    }
    
    /**
     * Obtain the main panel for placing in the centre of the GUI.
     * @return the main panel.
     */
    public JPanel getMainPanel() {
        return mMainPanel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void tabPaneAdded(final String databaseName, final JTabbedPane tabbedPane) {
        LOGGER.info("Tab Pane added for database '" + databaseName + "'");
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                mMainPanel.add(tabbedPane, databaseName);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabPaneRemoved(final String databaseName, final JTabbedPane tabbedPane) {
        LOGGER.info("Tab Pane removed for database '" + databaseName + "'");
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                mMainPanel.remove(tabbedPane);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabPaneSwitched(final String databaseName, final JTabbedPane tabbedPane) {
        LOGGER.info("Tab Pane switch for database '" + databaseName + "'");
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                mCardLayout.show(mMainPanel, databaseName);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void hideTabPanes() {
        LOGGER.info("Hide all Tab Panes");
        
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (DefaultTabPaneManager.this) {
                    if (mIntroPanel == null) {
                        mIntroPanel = mSpringLoader.getBean("introPanel", IntroPanel.class);
                        mMainPanel.add(mIntroPanel, INTRO_PANEL_NAME);
                    }
                }
                mCardLayout.show(mMainPanel, INTRO_PANEL_NAME);
            }
        });
    }
}
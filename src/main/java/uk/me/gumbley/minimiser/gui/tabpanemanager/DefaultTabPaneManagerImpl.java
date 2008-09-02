package uk.me.gumbley.minimiser.gui.tabpanemanager;

import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.GUIUtils;

/**
 * Extends the core Tab Pane Manager functionality with GUI code.
 * 
 * @author matt
 *
 */
public final class DefaultTabPaneManagerImpl extends AbstractTabPaneManager {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultTabPaneManagerImpl.class);
    private static final String BLANK_PANEL_NAME = "*special*blank*panel*";

    private JPanel mainPanel;
    private JPanel blankPanel;
    
    private CardLayout cardLayout;
    
    /**
     * Construct the main panel with the card layout that'll accommodate the
     * tabbed panes.
     */
    public DefaultTabPaneManagerImpl() {
        super();
        mainPanel = new JPanel();
        blankPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.add(blankPanel, BLANK_PANEL_NAME);
        cardLayout.show(mainPanel, BLANK_PANEL_NAME);
    }
    
    /**
     * Obtain the main panel for placing in the centre of the GUI.
     * @return the main panel.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void tabPaneAdded(final String databaseName, final JTabbedPane tabbedPane) {
        LOGGER.info("Tab Pane added for database '" + databaseName + "'");
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                mainPanel.add(tabbedPane, databaseName);
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
                mainPanel.remove(tabbedPane);
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
                cardLayout.show(mainPanel, databaseName);
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
                cardLayout.show(mainPanel, BLANK_PANEL_NAME);
            }
        });
    }
}

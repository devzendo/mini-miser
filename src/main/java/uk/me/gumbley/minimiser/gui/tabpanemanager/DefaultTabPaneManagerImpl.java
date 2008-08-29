package uk.me.gumbley.minimiser.gui.tabpanemanager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.version.AppVersion;

/**
 * Extends the core Tab Pane Manager functionality with GUI code.
 * 
 * @author matt
 *
 */
public final class DefaultTabPaneManagerImpl extends AbstractTabPaneManager {
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
        blankPanel = createBlankPanel();
        cardLayout = new CardLayout(5, 5);
        mainPanel.setLayout(cardLayout);
        cardLayout.addLayoutComponent(blankPanel, BLANK_PANEL_NAME);
        //cardLayout.show(mainPanel, BLANK_PANEL_NAME);
        //cardLayout.invalidateLayout(mainPanel);
    }

    private JPanel createBlankPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JLabel label = new JLabel(AppName.getAppName() + " v" + AppVersion.getVersion());
        panel.add(label, BorderLayout.CENTER);
        return panel;
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
        cardLayout.addLayoutComponent(tabbedPane, databaseName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabPaneRemoved(final String databaseName, final JTabbedPane tabbedPane) {
        cardLayout.removeLayoutComponent(tabbedPane);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabPaneSwitched(final String databaseName, final JTabbedPane tabbedPane) {
        cardLayout.show(mainPanel, databaseName);
    }

    /**
     * {@inheritDoc}
     */
    public void hideTabPanes() {
        cardLayout.show(mainPanel, BLANK_PANEL_NAME);
    }
}

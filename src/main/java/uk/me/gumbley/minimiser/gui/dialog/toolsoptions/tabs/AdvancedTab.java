package uk.me.gumbley.minimiser.gui.dialog.toolsoptions.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import uk.me.gumbley.minimiser.gui.dialog.toolsoptions.ToolsOptionsTab;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * The Advanced Tools->Options tab holds settings suitable for gurus.
 * 
 * @author matt
 *
 */
public final class AdvancedTab implements ToolsOptionsTab {
    private Prefs changeCollectingPrefs;
    private JPanel mainPanel;
    private Object lock;

    /**
     * Create the Advanced Tab
     * @param collectingPrefs the Prefs that will collect changes until
     * committed.
     */
    public AdvancedTab(final Prefs collectingPrefs) {
        lock = new Object();
        synchronized (lock) {
            this.changeCollectingPrefs = collectingPrefs;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Advanced";
    }
    
    /**
     * {@inheritDoc}
     */
    public void initComponent() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        final JPanel innerPanel = new JPanel(new GridLayout(4, 1, 16, 16));
        
        final JCheckBox sqlViewEnabled = new JCheckBox("SQL view enabled?"); 
        synchronized (lock) {
            sqlViewEnabled.setSelected(!changeCollectingPrefs.isTabHidden(TabIdentifier.SQL.toString()));
        }
        sqlViewEnabled.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final boolean enabled = sqlViewEnabled.isSelected();
                synchronized (lock) {
                    if (enabled) {
                        changeCollectingPrefs.clearTabHidden(TabIdentifier.SQL.toString());
                    } else {
                        changeCollectingPrefs.setTabHidden(TabIdentifier.SQL.toString());
                    }
                }
            }
        });
        
        innerPanel.add(sqlViewEnabled);
        
//        innerPanel.add(new JCheckBox("Does nothing 1"));
//        innerPanel.add(new JCheckBox("Does nothing 1"));
//        innerPanel.add(new JCheckBox("Does nothing 1"));
//
        mainPanel.add(innerPanel, BorderLayout.NORTH);
    }

    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return mainPanel;
    }
    
    /**
     * {@inheritDoc}
     */
    public void disposeComponent() {
        // do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public void destroy() {
        // do nothing
    }
}

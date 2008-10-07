package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * A developer-friendly (and moderately user-friendly, if you know SQL) SQL
 * investigation, debug and diagnosis tab.
 * <p>
 * No TDD here.
 *  
 * @author matt
 *
 */
public final class SQLTab implements Tab {
    
    private final DatabaseDescriptor databaseDescriptor;
    private volatile JPanel mainPanel;
    private JPanel outputPanel;

    /**
     * Construct the SQL tab
     * @param descriptor the database descriptor
     */
    public SQLTab(final DatabaseDescriptor descriptor) {
        databaseDescriptor = descriptor;
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
    public void initComponent() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(8, 8));
        
        outputPanel = new JPanel();
        mainPanel.add(outputPanel, BorderLayout.CENTER);
        
        final JPanel textOutputPanel = new JPanel();
        textOutputPanel.add(new JTextArea("A nice ASCII table view goes here"));
        final JPanel tableOutputPanel = new JPanel();
        tableOutputPanel.add(new JTable(20, 5)); // example!!
        final JTabbedPane outputTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        outputTabbedPane.add(textOutputPanel, "Console");
        outputTabbedPane.add(tableOutputPanel, "Table");
        outputPanel.add(outputTabbedPane);
        
        final JPanel entryPanel = new JPanel();
        entryPanel.add(new JTextArea("SQL> "));
        mainPanel.add(entryPanel, BorderLayout.SOUTH);
        
    }
    
    /**
     * {@inheritDoc}
     */
    public void destroy() {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}
     */
    public void disposeComponent() {
        // TODO Auto-generated method stub
        
    }
}

package uk.me.gumbley.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.logging.Logging;
import uk.me.gumbley.minimiser.gui.tab.impl.sql.SQLTab;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.persistence.impl.JdbcTemplateAccessFactoryImpl;
import uk.me.gumbley.minimiser.pluginmanager.DummyAppPluginManager;
import uk.me.gumbley.minimiser.pluginmanager.DummyAppPluginRegistry;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;


/**
 * @author matt
 *
 */
public final class DriveSQLTab {
    private static final Logger LOGGER = Logger
            .getLogger(DriveSQLTab.class);
    private DriveSQLTab() {
        // nop
    }
    /**
     * @param args command line
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure();
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        argList = Logging.getInstance().setupLoggingFromArgs(argList);
        
        GUIUtils.runOnEventThread(new Runnable() {

            public void run() {
                LOGGER.info("SQL Tab experimentation starting...");
                Beautifier.makeBeautiful();
                
                final JFrame frame = new JFrame("title");
                frame.setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setPreferredSize(new Dimension(800, 600));
                
                final CursorManager cursorManager = new CursorManager();
                cursorManager.setMainFrame(frame);
                
                LOGGER.info("Opening database");
                final PluginManager dummyPluginManager = new DummyAppPluginManager();
                final AccessFactory accessFactory = new JdbcTemplateAccessFactoryImpl(dummyPluginManager);
                final String dbPath = "/home/matt/Desktop/crap/clear-test-1/clear-test-1";
                final MiniMiserDAOFactory miniMiserDatabase = accessFactory.openDatabase(dbPath, "");
                final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("clear-test-1", dbPath);
                databaseDescriptor.setAttribute(AttributeIdentifier.Database, miniMiserDatabase);
                LOGGER.info("Database open");
                
                final PluginRegistry pluginRegistry = new DummyAppPluginRegistry();
                final SQLTab sqlTab = new SQLTab(databaseDescriptor, cursorManager, pluginRegistry);
                sqlTab.initComponent();
                frame.add(sqlTab.getComponent());
                
                frame.pack();
                frame.setVisible(true);
            }
            
        });
    }
}

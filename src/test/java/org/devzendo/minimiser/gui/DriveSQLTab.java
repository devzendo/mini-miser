package org.devzendo.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.tab.impl.sql.SQLTab;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.persistence.AccessFactory;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.persistence.impl.JdbcTemplateAccessFactoryImpl;
import org.devzendo.minimiser.pluginmanager.DummyAppPluginManager;
import org.devzendo.minimiser.pluginmanager.DummyAppPluginRegistry;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.util.InstancePair;
import org.devzendo.minimiser.util.InstanceSet;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.logging.Logging;


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
                final InstanceSet<DAOFactory> daoFactories = accessFactory.openDatabase(dbPath, "");
                final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("clear-test-1", dbPath);
                // Add the MiniMiserDAOFactory and other plugins'
                // DAOFactories to the DatabaseDescriptor
                for (InstancePair<DAOFactory> daoFactoryPair : daoFactories.asList()) {
                    databaseDescriptor.setDAOFactory(daoFactoryPair.getClassOfInstance(), daoFactoryPair.getInstance());
                }
                
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

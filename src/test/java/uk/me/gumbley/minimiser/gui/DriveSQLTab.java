package uk.me.gumbley.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.logging.Logging;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.tab.impl.sql.SQLTab;
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.impl.JdbcTemplateAccessFactoryImpl;
import uk.me.gumbley.minimiser.util.DelayedExecutor;
import uk.me.gumbley.minimiser.version.AppVersion;


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
                LOGGER.info(String.format("%s %s SQL Tab experimentation starting...", AppName.getAppName(), AppVersion.getVersion()));
                Beautifier.makeBeautiful();
                
                final JFrame frame = new JFrame("title");
                frame.setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                LOGGER.info("Opening database");
                AccessFactory accessFactory = new JdbcTemplateAccessFactoryImpl();
                final String dbPath = "/home/matt/Desktop/crap/clear-test-1/clear-test-1";
                MiniMiserDatabase miniMiserDatabase = accessFactory.openDatabase(dbPath, "");
                DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("clear-test-1", dbPath);
                databaseDescriptor.setAttribute(AttributeIdentifier.Database, miniMiserDatabase);
                LOGGER.info("Database open");
                
                SQLTab sqlTab = new SQLTab(databaseDescriptor);
                sqlTab.initComponent();
                frame.add(sqlTab.getComponent());
                
                frame.pack();
                frame.setVisible(true);
            }
            
        });
    }
}

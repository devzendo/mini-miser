/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.Beautifier;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commoncode.logging.Logging;
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
        List<String> argList = new ArrayList<String>(Arrays.asList(args));
        argList = Logging.getInstance().setupLoggingFromArgs(argList);
        final String dbPath = getDbPathFromCommandLine(argList);
        final String dbPassword = getDbPasswordFromCommandLine(argList);
        
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
                final InstanceSet<DAOFactory> daoFactories = accessFactory.openDatabase(dbPath, dbPassword);
                final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("clear-test-1", dbPath);
                // Add the MiniMiserDAOFactory and other plugins'
                // DAOFactories to the DatabaseDescriptor
                for (final InstancePair<DAOFactory> daoFactoryPair : daoFactories.asList()) {
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

    private static String getDbPasswordFromCommandLine(final List<String> argList) {
        String dbPassword = "";
        for (final String arg : argList) {
            final File dbDir = new File(arg);
            if (dbDir.exists() && dbDir.isDirectory()) {
                continue; // it's the db path
            } else {
                dbPassword = arg;
            }
        }
        LOGGER.info("Using '" + dbPassword + "' as the password");
        return dbPassword;
    }

    private static String getDbPathFromCommandLine(final List<String> argList) {
        String dbPath = null;
        for (final String arg : argList) {
            final File dbDir = new File(arg);
            if (dbDir.exists() && dbDir.isDirectory()) {
                dbPath = new File(dbDir, dbDir.getName()).getAbsolutePath();
                LOGGER.info("Opening " + dbPath + " in the SQL console");
                return dbPath;
            }
        }
        if (dbPath == null) {
            LOGGER.warn("No database directory specified on command line");
            System.exit(1);
        }
        return null;
    }
}

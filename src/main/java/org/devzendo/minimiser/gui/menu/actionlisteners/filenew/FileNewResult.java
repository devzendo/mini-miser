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

package org.devzendo.minimiser.gui.menu.actionlisteners.filenew;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.string.StringUtils;
import org.devzendo.minimiser.gui.CursorManager;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.persistence.AccessFactory;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.persistence.PersistenceObservableEvent;
import org.devzendo.minimiser.util.InstancePair;
import org.devzendo.minimiser.util.InstanceSet;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.springframework.dao.DataAccessException;


/**
 * The worker for creating databases when the File|New wizard has finished.
 * @author matt
 *
 */
public final class FileNewResult extends DeferredWizardResult {
    private static final Logger LOGGER = Logger.getLogger(FileNewResult.class);
    private static final int SETUP_STEPS = 6;
    private final OpenDatabaseList databaseList;
    private final AccessFactory access;
    private final CursorManager cursorMan;

    /**
     * Create the FileNewResult creation worker
     * @param openDatabaseList the open database list that will be added to
     * @param accessFactory the access factory to be used for creation
     * @param cursorManager allows us to set the hourglass/normal cursor
     */
    public FileNewResult(final OpenDatabaseList openDatabaseList,
            final AccessFactory accessFactory,
            final CursorManager cursorManager) {
        this.databaseList = openDatabaseList;
        this.access = accessFactory;
        this.cursorMan = cursorManager;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void start(final Map map, final ResultProgressHandle progress) {
        final Map<String, Object> pluginProperties = map;
        if (pluginProperties == null) {
            LOGGER.info("User cancelled File|New");
            return;
        }
        final int maxSteps = access.getNumberOfDatabaseCreationSteps(pluginProperties) + SETUP_STEPS;
        final AtomicInteger stepNo = new AtomicInteger(1);
        progress.setProgress("Starting creation", stepNo.incrementAndGet(), maxSteps);
        cursorMan.hourglassViaEventThread(this.getClass().getSimpleName());
        try {
            progress.setProgress("Converting params", stepNo.incrementAndGet(), maxSteps);
            final Boolean dbEncrypted = (Boolean) pluginProperties.get(FileNewWizardSecurityOptionPage.ENCRYPTED);
            if (!dbEncrypted) {
                pluginProperties.put(FileNewWizardSecurityOptionPage.PASSWORD, "");
            }
            final String dbPassword = (String) pluginProperties.get(FileNewWizardSecurityOptionPage.PASSWORD);
            final String dbPath = (String) pluginProperties.get(FileNewWizardChooseFolderPage.PATH_NAME);
            dumpWizardResults(pluginProperties);
            
            LOGGER.info("Creating database at " + dbPath);
            // need the db path to be /path/to/db/databasename/databasename
            // i.e. duplicate the directory component at the end
            progress.setProgress("Getting path", stepNo.incrementAndGet(), maxSteps);
    
            final File dbPathFile = new File(dbPath);
            final String dbName = dbPathFile.getName();
            final String dbFullPath = StringUtils.slashTerminate(dbPathFile.getAbsolutePath()) + dbName;
            LOGGER.info("Final db path is " + dbFullPath);
            progress.setProgress("Creating DB", stepNo.incrementAndGet(), maxSteps);
    
            final Observer<PersistenceObservableEvent> observer = new Observer<PersistenceObservableEvent>() {
                public void eventOccurred(final PersistenceObservableEvent observableEvent) {
                    LOGGER.info("DB creation progress: " + observableEvent.getDescription());
                    progress.setProgress(observableEvent.getDescription(), stepNo.incrementAndGet(), maxSteps);
                }
            };
            try {
                final InstanceSet<DAOFactory> daoFactories = access.createDatabase(dbFullPath, dbPassword, observer, pluginProperties);
                progress.setProgress("Updating GUI", stepNo.incrementAndGet(), maxSteps);
        
                LOGGER.info("Database created; adding to open database list");
                final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(dbName, dbFullPath);

                // Add the MiniMiserDAOFactory and other plugins'
                // DAOFactories to the DatabaseDescriptor
                for (InstancePair<DAOFactory> daoFactoryPair : daoFactories.asList()) {
                    databaseDescriptor.setDAOFactory(daoFactoryPair.getClassOfInstance(), daoFactoryPair.getInstance());
                }
                databaseList.addOpenedDatabase(databaseDescriptor);
    
                cursorMan.normalViaEventThread(this.getClass().getSimpleName());
                progress.finished(null);
            } catch (final DataAccessException dae) {
                LOGGER.warn("Failed to create database " + dbName + ": " + dae.getMessage(), dae);
                throw dae;
            }
        } finally {
            cursorMan.normalViaEventThread(this.getClass().getSimpleName());
        }
    }

    private void dumpWizardResults(final Map<String, Object> result) {
        LOGGER.info("Wizard results:");
        for (final String key : result.keySet()) {
            final Object valueObject = result.get(key);
            final String valueString = valueObject == null ? "" : valueObject.toString();
            final String value =
                key.equals(FileNewWizardSecurityOptionPage.PASSWORD) ?
                        StringUtils.maskSensitiveText(valueString) :
                        valueString;
            LOGGER.info(String.format("  Key %s: Value %s", key, value));
        }
    }
}

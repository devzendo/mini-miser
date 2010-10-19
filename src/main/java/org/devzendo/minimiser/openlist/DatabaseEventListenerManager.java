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

package org.devzendo.minimiser.openlist;

import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commonspring.springbeanlistloader.AbstractSpringBeanListLoaderImpl;
import org.devzendo.commonspring.springloader.SpringLoader;


/**
 * Manages a list of listeners of DatabaseEvents.
 * 
 * @author matt
 *
 */
public final class DatabaseEventListenerManager extends AbstractSpringBeanListLoaderImpl<Observer<DatabaseEvent>> {
    private static final Logger LOGGER = Logger
            .getLogger(DatabaseEventListenerManager.class);
    private final OpenDatabaseList openDatabaseList;

    /**
     * @param springLoader the Spring loader
     * @param databaseList the Open Database List
     * @param listenerBeanNames the list of ODL listener beans to manage.
     */
    public DatabaseEventListenerManager(final SpringLoader springLoader, final OpenDatabaseList databaseList, final List<String> listenerBeanNames) {
        super(springLoader, listenerBeanNames);
        this.openDatabaseList = databaseList;
    }

    /**
     * Wire the loaded listeners to the Open Database List
     */
    public void wire() {
        LOGGER.info("Wiring Open Database List Listeners");
        for (Observer<DatabaseEvent> observer : getBeans()) {
            LOGGER.debug("Wiring '" + observer.getClass().getName() + "'");
            openDatabaseList.addDatabaseEventObserver(observer);
        }
        LOGGER.info("Wired Open Database List Listeners");
    }
    
    /**
     * Unwire the loaded listeners from the Open Database List
     */
    public void unwire() {
        LOGGER.info("Unwiring Open Database List Listeners");
        for (Observer<DatabaseEvent> observer : getBeans()) {
            LOGGER.debug("Unwiring '" + observer.getClass().getName() + "'");
            openDatabaseList.removeDatabaseEventObserver(observer);
        }
        LOGGER.info("Unwired Open Database List Listeners");
    }
}

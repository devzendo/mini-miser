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

package org.devzendo.minimiser.opentablist;

import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commonspring.springbeanlistloader.AbstractSpringBeanListLoaderImpl;
import org.devzendo.commonspring.springloader.SpringLoader;


/**
 * Manages a list of listeners to TabEvents.
 * 
 * @author matt
 *
 */
public final class TabEventListenerManager extends
        AbstractSpringBeanListLoaderImpl<Observer<TabEvent>> {
    private static final Logger LOGGER = Logger
            .getLogger(TabEventListenerManager.class);

    private final OpenTabList openTabList;

    /**
     * @param springLoader
     *        the Spring loader
     * @param tabList
     *        the Open Tab List
     * @param listenerBeanNames
     *        the list of tab event listener beans to manage.
     */
    public TabEventListenerManager(
            final SpringLoader springLoader,
            final OpenTabList tabList,
            final List<String> listenerBeanNames) {
        super(springLoader, listenerBeanNames);
        this.openTabList = tabList;
    }

    /**
     * Wire the loaded listeners to the Open Tab List
     */
    public void wire() {
        LOGGER.info("Wiring Open Tab List Listeners");
        for (Observer<TabEvent> observer : getBeans()) {
            LOGGER.debug("Wiring '" + observer.getClass().getName() + "'");
            openTabList.addTabEventObserver(observer);
        }
        LOGGER.info("Wired Open Tab List Listeners");
    }

    /**
     * Unwire the loaded listeners from the Open Tab List
     */
    public void unwire() {
        LOGGER.info("Unwiring Open Tab List Listeners");
        for (Observer<TabEvent> observer : getBeans()) {
            LOGGER.debug("Unwiring '" + observer.getClass().getName() + "'");
            openTabList.removeTabEventObserver(observer);
        }
        LOGGER.info("Unwired Open Tab List Listeners");
    }
}

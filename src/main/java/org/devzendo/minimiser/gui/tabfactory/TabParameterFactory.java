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

package org.devzendo.minimiser.gui.tabfactory;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.tab.TabParameter;
import org.springframework.beans.factory.FactoryBean;

/**
 * A Spring FactoryBean for storing the current tab parameter as
 * available during an open operation.
 *
 * This is not a factory in the sense that it creates TabParameters,
 * rather, it's just used as a temporary stash during an open, so that
 * the tab views can get hold of the TabParameter via the
 * application context.
 *
 * The current TabParameter will be removed outside of an open.
 *
 * This is a Prototype, not a Singleton.
 *
 * @author matt
 *
 */
public final class TabParameterFactory implements FactoryBean<TabParameter> {
    private static final Logger LOGGER = Logger.getLogger(TabParameterFactory.class);
    private TabParameter mFactoryTabParameter;

    /**
     * {@inheritDoc}
     */
    public TabParameter getObject() throws Exception {
        LOGGER.debug(String.format("TabParameterFactory returning %s as TabParameter object", mFactoryTabParameter));
        return mFactoryTabParameter;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getObjectType() {
        return TabParameter.class;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return false;
    }

    /**
     * Factory population method
     * @param parameter the TabParameter to return
     */
    public void setTabParameter(final TabParameter parameter) {
        LOGGER.debug(String.format("TabParameterFactory being populated with %s as TabParameter object", parameter));
        mFactoryTabParameter = parameter;
    }

    /**
     * Clear out the tab parameter
     */
    public void clearTabParameter() {
        mFactoryTabParameter = null;
    }
}

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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean for obtaining the singleton MainFrameTitle - the application's
 * main window's title bar controller.
 * 
 * @author matt
 *
 */
public final class MainFrameTitleFactory implements FactoryBean {
    private static final Logger LOGGER = Logger
            .getLogger(MainFrameTitleFactory.class);
    private MainFrameTitle factoryMainFrameTitle;
    
    /**
     * {@inheritDoc}
     */
    public Object getObject() throws Exception {
        LOGGER.debug("MainFrameTitleFactory returning main frame title as MainFrameTitle object");
        return factoryMainFrameTitle;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getObjectType() {
        return MainFrameTitle.class;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * Factory population method
     * @param frameTitle the MainFrameTitle to return as a Singleton
     */
    public void setMainFrameTitle(final MainFrameTitle frameTitle) {
        LOGGER.debug("MainFrameTitleFactory being populated with MainFrameTitle object");
        factoryMainFrameTitle = frameTitle;
    }
}

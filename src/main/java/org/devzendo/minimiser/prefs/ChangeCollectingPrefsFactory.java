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

package org.devzendo.minimiser.prefs;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

/**
 * A Spring FactoryBean for storing the change-collecting Preds as
 * available during the activities of the Tools->Options dialog.
 * 
 * This is not a factory in the sense that it creates Prefs,
 * rather, it's just used as a temporary stash during the loading of the
 * Tools->Options tab beans, so that they can get hold of the Prefs via the
 * application context.
 * 
 * The Change-Collecting Prefs will be removed outside of an open.
 * 
 * This is a Prototype, not a Singleton.
 * 
 * @author matt
 *
 */
public final class ChangeCollectingPrefsFactory implements FactoryBean {
    private static final Logger LOGGER = Logger.getLogger(ChangeCollectingPrefsFactory.class);
    private Prefs factoryPrefs;

    /**
     * {@inheritDoc}
     */
    public Object getObject() throws Exception {
        LOGGER.debug(String.format("ChangeCollectingPrefsFactory returning %s as Prefs object", factoryPrefs));
        return factoryPrefs;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getObjectType() {
        return Prefs.class;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return false;
    }
    
    /**
     * Factory population method
     * @param prefs the Prefs to return
     */
    public void setChangeCollectingPrefs(final Prefs prefs) {
        LOGGER.debug(String.format("ChangeCollectingPrefsFactory being populated with %s as Prefs object", prefs));
        factoryPrefs = prefs;
    }

    /**
     * Clear out the prefs
     */
    public void clearChangeCollectingPrefs() {
        factoryPrefs = null;
    }
}

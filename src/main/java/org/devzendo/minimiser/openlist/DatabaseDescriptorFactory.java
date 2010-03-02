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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

/**
 * A Spring FactoryBean for storing the current database descriptor as
 * available during an open operation.
 * 
 * This is not a factory in the sense that it creates DatabaseDescriptors,
 * rather, it's just used as a temporary stash during an open, so that
 * the tab views can get hold of the DatabaseDescriptor via the
 * application context.
 * 
 * The current DatabaseDescriptor will be removed outside of an open.
 * 
 * This is a Prototype, not a Singleton.
 * 
 * @author matt
 *
 */
public final class DatabaseDescriptorFactory implements FactoryBean {
    private static final Logger LOGGER = Logger.getLogger(DatabaseDescriptorFactory.class);
    private DatabaseDescriptor factoryDatabaseDescriptor;

    /**
     * {@inheritDoc}
     */
    public Object getObject() throws Exception {
        LOGGER.debug(String.format("DatabaseDescriptorFactory returning %s as DatabaseDescriptor object", factoryDatabaseDescriptor));
        return factoryDatabaseDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getObjectType() {
        return DatabaseDescriptor.class;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return false;
    }
    
    /**
     * Factory population method
     * @param descriptor the DatabaseDescriptor to return
     */
    public void setDatabaseDescriptor(final DatabaseDescriptor descriptor) {
        LOGGER.debug(String.format("DatabaseDescriptorFactory being populated with %s as DatabaseDescriptor object", descriptor));
        factoryDatabaseDescriptor = descriptor;
    }

    /**
     * Clear out the database descriptor
     */
    public void clearDatabaseDescriptor() {
        factoryDatabaseDescriptor = null;
    }
}

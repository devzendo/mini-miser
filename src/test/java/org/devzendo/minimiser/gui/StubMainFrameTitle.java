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


/**
 * Stub main frame title controller.
 * 
 * @author matt
 *
 */
public final class StubMainFrameTitle implements MainFrameTitle {
    private String mDatabaseName = null;
    private String mApplicationName = null;

    /**
     * {@inheritDoc}
     */
    public void clearCurrentDatabaseName() {
        mDatabaseName = null;
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentDatabaseName() {
        return mDatabaseName;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentDatabaseName(final String databasename) {
        mDatabaseName = databasename;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationName(final String applicationName) {
        mApplicationName = applicationName;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return mApplicationName;
    }
}

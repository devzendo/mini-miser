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

/**
 * 
 */
package org.devzendo.minimiser.persistence;

import java.util.List;

import javax.sql.DataSource;

import org.devzendo.minimiser.plugin.AbstractPlugin;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.facade.closedatabase.DatabaseClosing;
import org.devzendo.minimiser.plugin.facade.closedatabase.DatabaseClosingFacade;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;


/**
 * @author matt
 *
 */
public final class DatabaseClosingAppPlugin extends AbstractPlugin implements
        ApplicationPlugin, DatabaseClosing {
    private final DatabaseClosingFacade mDatabaseClosingFacade;

    private boolean mCloseDatabaseCalled;
    
    /**
     * instantiate all the facades
     */
    public DatabaseClosingAppPlugin() {
        mDatabaseClosingFacade = new DatabaseClosingFacade() {
            public void closeDatabase(
                    final DataSource dataSource,
                    final SimpleJdbcTemplate jdbcTemplate) throws DataAccessException {
                mCloseDatabaseCalled = true;
            }
        };
    }
    
    /**
     * {@inheritDoc}
     */
    public String getAboutDetailsResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getChangeLogResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDevelopersContactDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getFullLicenceDetailsResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getIntroPanelBackgroundGraphicResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getShortLicenseDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getUpdateSiteBaseURL() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getApplicationContextResourcePaths() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "DatabaseClosingAppPlugin";
    }

    /**
     * {@inheritDoc}
     */
    public String getSchemaVersion() {
        return "1.0";
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return "1.0";
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }

    /**
     * @return true iff the close method has been called
     */
    public boolean closeMethodCalled() {
        return mCloseDatabaseCalled;
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseClosingFacade getDatabaseClosingFacade() {
        return mDatabaseClosingFacade;
    }
}

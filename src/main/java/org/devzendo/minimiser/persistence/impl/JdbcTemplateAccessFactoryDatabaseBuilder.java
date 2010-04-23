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
package org.devzendo.minimiser.persistence.impl;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.persistence.PersistenceObservableEvent;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * Given the initial settings for a database, create a DataSource and
 * SimpleJdbcTemplate for it.
 * 
 * @author matt
 *
 */
class JdbcTemplateAccessFactoryDatabaseBuilder {
    private static final Logger LOGGER = Logger.getLogger(JdbcTemplateAccessFactoryDatabaseBuilder.class);
    private final String mDbPassword;
    private final String mDbPath;
    private final String mDbURL;
    private final SingleConnectionDataSource mDataSource;
    private final SimpleJdbcTemplate mJdbcTemplate;
    
    /**
     * Create the DataSource and SimpleJdbcTemplate for a given database.
     * 
     * @param databasePath the path to the database 
     * @param password the password of the database, possibly null or empty 
     * @param allowCreate true to create a nonexistant database; false to
     * prevent creation
     * @param observer an observer to notify of connection building progress
     */
    public JdbcTemplateAccessFactoryDatabaseBuilder(
            final String databasePath,
            final String password,
            final boolean allowCreate,
            final Observer<PersistenceObservableEvent> observer) {
        LOGGER.debug("Validating arguments");
        if (databasePath == null) {
            throw new DataAccessResourceFailureException("Null database path");
        }
        mDbPath = databasePath.trim();
        if (mDbPath.length() == 0) {
            throw new DataAccessResourceFailureException(String.format("Incorrect database path '%s'", databasePath));
        }
        mDbPassword = (password == null) ? "" : password;
        String dbURL = mDbPassword.length() == 0 ?
                String.format("jdbc:h2:%s", mDbPath) :
                String.format("jdbc:h2:%s;CIPHER=AES", mDbPath);
        if (!allowCreate) {
            dbURL += ";IFEXISTS=TRUE";
        }
        mDbURL = dbURL;

        if (observer != null) {
            observer.eventOccurred(new PersistenceObservableEvent("Preparing database connectivity"));
        }
        LOGGER.debug("Obtaining data source bean");
        final String driverClassName = "org.h2.Driver";
        final String userName = "sa";
        final boolean suppressClose = false;
        mDataSource = new SingleConnectionDataSource(driverClassName,
            mDbURL, userName, mDbPassword + " userpwd", suppressClose);
        LOGGER.debug("DataSource is " + mDataSource);

        if (observer != null) {
            observer.eventOccurred(new PersistenceObservableEvent("Opening database"));
        }
        LOGGER.debug("Obtaining SimpleJdbcTemplate");
        mJdbcTemplate = new SimpleJdbcTemplate(mDataSource);
        if (observer != null) {
            observer.eventOccurred(new PersistenceObservableEvent("Database opened"));
        }
        LOGGER.debug("Database setup done");
    }
    
    /**
     * @return the database path
     */
    public String getDbPath() {
        return mDbPath;
    }
    
    /**
     * @return the database URL
     */
    public String getDbURL() {
        return mDbURL;
    }
    
    /**
     * @return the database SimpleJdbcTemplate
     */
    public SimpleJdbcTemplate getJdbcTemplate() {
        return mJdbcTemplate;
    }
    
    /**
     * @return the database DataSource
     */
    public SingleConnectionDataSource getDataSource() {
        return mDataSource;
    }
}
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

package org.devzendo.minimiser.persistence.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.persistence.dao.SequenceDao;
import org.devzendo.minimiser.persistence.dao.VersionsDao;
import org.devzendo.minimiser.persistence.dao.impl.JdbcTemplateSequenceDao;
import org.devzendo.minimiser.persistence.dao.impl.JdbcTemplateVersionsDao;
import org.devzendo.minimiser.persistence.sql.SQLAccess;
import org.devzendo.minimiser.persistence.sql.impl.H2SQLAccess;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;


/**
 * A MiniMiser DAO factory that uses a SimpleJdbcTemplate
 * 
 * @author matt
 *
 */
public final class JdbcTemplateMiniMiserDAOFactoryImpl implements MiniMiserDAOFactory {
    private static final Logger LOGGER = Logger.getLogger(JdbcTemplateMiniMiserDAOFactoryImpl.class);
    
    @SuppressWarnings("unused")
    private final String dbURL;
    private final String dbPath;
    private final SimpleJdbcTemplate jdbcTemplate;
    private volatile boolean isClosed = true;
    private final VersionsDao versionsDao;
    private final SequenceDao sequenceDao;
    private final DataSource dataSource;
    private SQLAccess sqlAccess;

    /**
     * @param url the database URL
     * @param path the path to the database for display
     * @param template the SimpleJdbcTemplate to access this database with
     * @param source the dataSource to access this databse with
     */
    public JdbcTemplateMiniMiserDAOFactoryImpl(final String url, final String path, final SimpleJdbcTemplate template, final DataSource source) {
        this.dbURL = url;
        this.dbPath = path;
        this.jdbcTemplate = template;
        this.dataSource = source;
        isClosed = false;
        versionsDao = new JdbcTemplateVersionsDao(jdbcTemplate);
        sequenceDao = new JdbcTemplateSequenceDao(jdbcTemplate);
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (isClosed) {
            LOGGER.info("Database at '" + dbPath + "' is already closed");
            return;
        }
        try {
            LOGGER.info("Closing database at '" + dbPath + "'");
            DataSourceUtils.getConnection(dataSource).close();
            isClosed = true;
        } catch (final CannotGetJdbcConnectionException e) {
            LOGGER.warn("Can't get JDBC Connection on close: " + e.getMessage(), e);
        } catch (final SQLException e) {
            LOGGER.warn("SQL Exception on close: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public VersionsDao getVersionDao() {
        checkClosed("getVersionDao");
        return versionsDao;
    }

    /**
     * {@inheritDoc}
     */
    public SequenceDao getSequenceDao() {
        checkClosed("getSequenceDao");
        return sequenceDao;
    }
    
    private void checkClosed(final String method) {
        if (isClosed) {
            throw new IllegalStateException(String.format("Cannot call %s with a closed database", method));
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isClosed() {
        if (isClosed) {
            return true;
        }
        try {
            return DataSourceUtils.getConnection(dataSource).isClosed();
        } catch (final CannotGetJdbcConnectionException e) {
            LOGGER.warn("Can't get JDBC Connection on isClosed: " + e.getMessage(), e);
        } catch (final SQLException e) {
            LOGGER.warn("SQL Exception on isClosed: " + e.getMessage(), e);
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public SQLAccess getSQLAccess() {
        synchronized (this) {
            checkClosed("getSQLAccess");
            if (sqlAccess == null) {
                sqlAccess = new H2SQLAccess(dataSource, jdbcTemplate);
            }
            return sqlAccess;
        }
    }
}

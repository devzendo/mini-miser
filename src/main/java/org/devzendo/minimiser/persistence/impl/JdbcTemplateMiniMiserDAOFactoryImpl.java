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
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.persistence.dao.SequenceDao;
import org.devzendo.minimiser.persistence.dao.VersionsDao;
import org.devzendo.minimiser.persistence.dao.impl.JdbcTemplateSequenceDao;
import org.devzendo.minimiser.persistence.dao.impl.JdbcTemplateVersionsDao;
import org.devzendo.minimiser.persistence.sql.SQLAccess;
import org.devzendo.minimiser.persistence.sql.impl.H2SQLAccess;
import org.devzendo.minimiser.plugin.facade.closedatabase.DatabaseClosing;
import org.devzendo.minimiser.plugin.facade.closedatabase.DatabaseClosingFacade;
import org.devzendo.minimiser.pluginmanager.PluginManager;
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
    private final String mDbURL;
    private final String mDbPath;
    private final SimpleJdbcTemplate mJdbcTemplate;
    private volatile boolean mIsClosed = true;
    private final VersionsDao versionsDao;
    private final SequenceDao sequenceDao;
    private final DataSource mDataSource;
    private final PluginManager mPluginManager;
    private SQLAccess mSqlAccess;
    private final Object mLock = new Object();

    /**
     * @param url the database URL
     * @param path the path to the database for display
     * @param template the SimpleJdbcTemplate to access this database with
     * @param source the dataSource to access this database with
     * @param pluginManager the pluginManager used to find the plugins that
     * implement DatabaseClosing, when closing.
     */
    public JdbcTemplateMiniMiserDAOFactoryImpl(
            final String url, final String path, final SimpleJdbcTemplate template,
            final DataSource source, final PluginManager pluginManager) {
        synchronized (mLock) {
            mDbURL = url;
            mDbPath = path;
            mJdbcTemplate = template;
            mDataSource = source;
            mPluginManager = pluginManager;
            mIsClosed = false;
            versionsDao = new JdbcTemplateVersionsDao(mJdbcTemplate);
            sequenceDao = new JdbcTemplateSequenceDao(mJdbcTemplate);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        synchronized (mLock) {
            if (mIsClosed) {
                LOGGER.info("Database at '" + mDbPath + "' is already closed");
                return;
            }
            callDatabaseClosingFacades();
            try {
                LOGGER.info("Closing database at '" + mDbPath + "'");
                DataSourceUtils.getConnection(mDataSource).close();
                mIsClosed = true;
            } catch (final CannotGetJdbcConnectionException e) {
                LOGGER.warn("Can't get JDBC Connection on close: " + e.getMessage(), e);
            } catch (final SQLException e) {
                LOGGER.warn("SQL Exception on close: " + e.getMessage(), e);
            }
        }
    }

    private void callDatabaseClosingFacades() {
        final List<DatabaseClosing> databaseClosingPlugins = mPluginManager.getPluginsImplementingFacade(DatabaseClosing.class);
        for (final DatabaseClosing databaseClosingPlugin : databaseClosingPlugins) {
            final DatabaseClosingFacade databaseClosingFacade = databaseClosingPlugin.getDatabaseClosingFacade();
            if (databaseClosingFacade == null) {
                LOGGER.warn(
                    "DatabaseClosing class "
                    + databaseClosingPlugin.getClass().getName()
                    + " returned a null facade - ignoring");
            } else {
                LOGGER.debug("Plugin " + databaseClosingPlugin.getClass().getName() + " tidying up in database");
                databaseClosingFacade.closeDatabase(mDataSource, mJdbcTemplate);
            }
        }        
    }

    /**
     * {@inheritDoc}
     */
    public VersionsDao getVersionDao() {
        synchronized (mLock) {
            checkClosed("getVersionDao");
            return versionsDao;
        }
    }

    /**
     * {@inheritDoc}
     */
    public SequenceDao getSequenceDao() {
        synchronized (mLock) {
            checkClosed("getSequenceDao");
            return sequenceDao;
        }
    }
    
    private void checkClosed(final String method) {
        if (mIsClosed) {
            throw new IllegalStateException(String.format("Cannot call %s with a closed database", method));
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isClosed() {
        synchronized (mLock) {
            if (mIsClosed) {
                return true;
            }
            try {
                return DataSourceUtils.getConnection(mDataSource).isClosed();
            } catch (final CannotGetJdbcConnectionException e) {
                LOGGER.warn("Can't get JDBC Connection on isClosed: " + e.getMessage(), e);
            } catch (final SQLException e) {
                LOGGER.warn("SQL Exception on isClosed: " + e.getMessage(), e);
            }
            return false;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public SQLAccess getSQLAccess() {
        synchronized (mLock) {
            checkClosed("getSQLAccess");
            if (mSqlAccess == null) {
                mSqlAccess = new H2SQLAccess(mDataSource, mJdbcTemplate);
            }
            return mSqlAccess;
        }
    }
}

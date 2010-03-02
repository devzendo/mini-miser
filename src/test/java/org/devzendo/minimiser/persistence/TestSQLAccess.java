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

package org.devzendo.minimiser.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.persistence.sql.SQLAccess;
import org.devzendo.minimiser.persistence.sql.SQLAccess.ResultType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;



/**
 * Tests the low-level access to the database, for migration, parsing SQL, etc.
 * @author matt
 *
 */
public final class TestSQLAccess extends DummyAppPluginManagerPersistenceUnittestCase {
    private static final Logger LOGGER = Logger.getLogger(TestSQLAccess.class);
    
    /**
     * 
     */
    @Test
    public void selectParsedOK() {
        final String dbName = "selectparsedok";
        final String dbPassword = "";
        doSimpleCreateDatabaseBoilerPlate(getAccessFactory(), dbName, dbPassword, new RunOnMiniMiserDatabase() {
            
            public void runOnMiniMiserDatabase(final MiniMiserDAOFactory mmData) {
                final SQLAccess sqlAccess = mmData.getSQLAccess();
                final ResultType type = sqlAccess.parse("SELECT * FROM Versions");
                Assert.assertSame(ResultType.ResultSet, type);
            }
            
        });
    }
    
    /**
     * 
     */
    @Test
    public void selectReturnsDataViaJdbc() {
        final String dbName = "selectreturnsdataviajdbc";
        final String dbPassword = "";
        
        doSimpleCreateDatabaseBoilerPlate(getAccessFactory(), dbName, dbPassword, new RunOnMiniMiserDatabase() {
            
            public void runOnMiniMiserDatabase(final MiniMiserDAOFactory mmData) {
                Statement statement = null;
                try {
                    final SQLAccess sqlAccess = mmData.getSQLAccess();
                    
                    statement = sqlAccess.createStatement();
                    final ResultSet rs = statement.executeQuery("SELECT * FROM Versions");
                    Assert.assertTrue(rs.first());
                    Assert.assertEquals(1, rs.getRow());
                } catch (final SQLException e) {
                    LOGGER.warn("Could not execute SQL", e);
                } finally {
                    try {
                        if (statement != null) {
                            statement.close();
                        }
                    } catch (final SQLException e1) {
                        LOGGER.warn("Could not close statement", e1);
                    }
                }
            }
            
        });
    }
    
    /**
     * 
     */
    @Test
    public void selectReturnsDataViaJdbcTemplate() {
        final String dbName = "selectreturnsdataviajdbctemplate";
        final String dbPassword = "";
        
        doSimpleCreateDatabaseBoilerPlate(getAccessFactory(), dbName, dbPassword, new RunOnMiniMiserDatabase() {
            
            public void runOnMiniMiserDatabase(final MiniMiserDAOFactory mmData) {
                final SQLAccess sqlAccess = mmData.getSQLAccess();
                final SimpleJdbcTemplate template = sqlAccess.getSimpleJdbcTemplate();
                final int count = template.queryForInt(
                    "select count(*) from Versions");
                LOGGER.info("There are " + count + " entries in Versions");
                Assert.assertTrue(count == 4);
            }
            
        });
    }
    
    /**
     * 
     */
    @Test
    public void dataSourceAvailable() {
        final String dbName = "datasourceavailable";
        final String dbPassword = "";
        
        doSimpleCreateDatabaseBoilerPlate(getAccessFactory(), dbName, dbPassword, new RunOnMiniMiserDatabase() {
            
            public void runOnMiniMiserDatabase(final MiniMiserDAOFactory mmData) {
                final SQLAccess sqlAccess = mmData.getSQLAccess();
                final DataSource dataSource = sqlAccess.getDataSource();
                try {
                    Assert.assertFalse(dataSource.getConnection().isClosed());
                } catch (final SQLException e) {
                    final String warning = "Caught unexpected exception when detecting that the database was not closed:" + e.getMessage();
                    LOGGER.warn(warning, e);
                    Assert.fail(warning);
                }
            }
            
        });
    }
}

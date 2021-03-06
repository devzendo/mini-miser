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

package org.devzendo.minimiser.gui.tab.impl.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.string.StringUtils;
import org.devzendo.minimiser.gui.console.output.OutputConsole;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.persistence.sql.BadSQLException;
import org.devzendo.minimiser.persistence.sql.SQLAccess;
import org.devzendo.minimiser.persistence.sql.SQLAccessException;
import org.devzendo.minimiser.tabledisplay.TableDisplay;


/**
 * A CommandHandler for SQL commands.
 * 
 * @author matt
 *
 */
final class SQLCommandHandler implements CommandHandler {
    private static final Logger LOGGER = Logger
            .getLogger(SQLCommandHandler.class);
    private final OutputConsole outputConsole;
    private final DatabaseDescriptor databaseDescriptor;
    private SQLAccess sqlAccess;
    private final TableDisplay[] tableDisplays;

    /**
     * @param console the output console
     * @param database the database to use the SQL commands against
     * @param displays a number of the table display abstractions
     */
    public SQLCommandHandler(final OutputConsole console, final DatabaseDescriptor database, final TableDisplay ...displays) {
        this.outputConsole = console;
        this.tableDisplays = displays;
        this.databaseDescriptor = database;
        final MiniMiserDAOFactory miniMiserDatabase = databaseDescriptor.getDAOFactory(MiniMiserDAOFactory.class);
        try {
            sqlAccess = miniMiserDatabase.getSQLAccess();
        } catch (final SQLAccessException sqle) {
            final String warning = "Could not get SQL Access: " + sqle.getMessage();
            LOGGER.warn(warning, sqle);
            console.warn(warning);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean handleCommand(final String command) {
        if (sqlAccess != null) {
            try {
                final SQLAccess.ResultType type = sqlAccess.parse(command);
                LOGGER.info("The ResultType from parse is " + type);
                switch (type) {
                    case Number: return handleNumber(command);
                    case RowCount: return handleRowCount(command);
                    case SuccessFailure: return handleSuccessFailure(command);
                    case ResultSet: return handleResultSet(command);
                    default:
                        return false;
                }
            } catch (final BadSQLException bsqle) {
                LOGGER.warn(bsqle.getMessage(), bsqle);
                outputConsole.warn(bsqle.getMessage());
            } catch (final SQLAccessException sqle) {
                LOGGER.warn(sqle.getMessage(), sqle);
                outputConsole.warn(sqle.getMessage());
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getIntroText() {
        return Arrays.asList(new String[] {
                "help [anything] - show H2's online help for anything",
                "<H2 SQL commands> - execute H2 SQL commands (see help [anything], above)",
        }); 
    }
    
    /**
     * Mini Template Method [GoF] for "doing JDBC stuff" and having exceptions
     * handled in a common manner, suitable for the SQL tab.
     * 
     * @author matt
     *
     */
    private abstract class StatementTemplate {
        private final Statement statement;
        public StatementTemplate() {
            statement = sqlAccess.createStatement();
        }
        public boolean execute() {
            try {
                return performExecution();
            } catch (final SQLException e) {
                final String warning = e.getMessage() + "; SQL Error Code: " + e.getErrorCode();
                LOGGER.warn(warning, e);
                outputConsole.warn(warning);
                return true;
            } finally {
                try {
                    statement.close();
                } catch (final SQLException c) {
                    final String warning = "Could not close statement: " + c.getMessage() + "; SQL Error Code: " + c.getErrorCode();
                    LOGGER.warn(warning, c);
                    outputConsole.warn(warning);
                    return true;
                }
            }
        }
        public abstract boolean performExecution() throws SQLException;
        protected final Statement getStatement() {
            return statement;
        }
    }


    private boolean handleResultSet(final String command) {
        return new StatementTemplate() {

            @Override
            public boolean performExecution() throws SQLException {
                final ResultSet resultSet = getStatement().executeQuery(command);
                final ResultSetMetaData metaData = resultSet.getMetaData();
                final List<String> headingsFromMetaData = getHeadingsFromMetaData(metaData);
                for (final TableDisplay tableDisplay : tableDisplays) {
                    tableDisplay.setHeadings(headingsFromMetaData);
                }
                if (resultSet.first()) {
                    do {
                        final List<String> stringifiedResultSet = stringifyResultSet(resultSet, metaData);
                        for (final TableDisplay tableDisplay : tableDisplays) {
                            tableDisplay.addRow(stringifiedResultSet);
                        }
                    } while (resultSet.next());
                    for (final TableDisplay tableDisplay : tableDisplays) {
                        tableDisplay.finished();
                    }
                } else {
                    outputConsole.info("No rows returned");
                }
                return true;
            }
        } .execute();
    }

    private boolean handleSuccessFailure(final String command) {
        return new StatementTemplate() {

            @Override
            public boolean performExecution() throws SQLException {
                getStatement().execute(command);
                outputConsole.info("ok");
                return true;
            }
        } .execute();
    }

    private boolean handleRowCount(final String command) {
        return new StatementTemplate() {

            @Override
            public boolean performExecution() throws SQLException {
                final int count = getStatement().executeUpdate(command);
                outputConsole.info(count + " " + StringUtils.pluralise("row", count) + " updated");
                return true;
            }
        } .execute();
    }

    private boolean handleNumber(final String command) {
        return new StatementTemplate() {

            @Override
            public boolean performExecution() throws SQLException {
                final int count = getStatement().executeUpdate(command);
                outputConsole.info("Value: " + count);
                return true;
            }
        } .execute();
    }

    private List<String> stringifyResultSet(final ResultSet resultSet, final ResultSetMetaData metaData) throws SQLException {
        final List<String> rowStrings = new ArrayList<String>();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            final Object colObject = resultSet.getObject(i + 1);
            
            if (resultSet.wasNull()) {
                rowStrings.add("NULL");
            } else {
                if (colObject == null) {
                    rowStrings.add("null");
                } else {
                    rowStrings.add(colObject.toString());
                }
            }
        }
        return rowStrings;
    }

    private List<String> getHeadingsFromMetaData(final ResultSetMetaData metaData) {
        final ArrayList<String> headings = new ArrayList<String>();
        try {
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                headings.add(metaData.getColumnName(i + 1));
            }
        } catch (final SQLException e) {
            LOGGER.warn("Can't process metadata: " + e.getMessage() + "; SQL error code " + e.getErrorCode(), e);
        }
        return headings;
    }
}

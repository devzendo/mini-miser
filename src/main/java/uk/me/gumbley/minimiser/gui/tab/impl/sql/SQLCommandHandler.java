    package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.gui.console.output.OutputConsole;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.sql.BadSQLException;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccess;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccessException;
import uk.me.gumbley.minimiser.tabledisplay.TableDisplay;

/**
 * A CommandHandler for SQL commands.
 * 
 * @author matt
 *
 */
public final class SQLCommandHandler implements CommandHandler {
    private static final Logger LOGGER = Logger
            .getLogger(SQLCommandHandler.class);
    private final OutputConsole outputConsole;
    private final DatabaseDescriptor databaseDescriptor;
    private SQLAccess sqlAccess;
    private TableDisplay[] tableDisplays;

    /**
     * @param database the database to use the SQL commands against
     * @param console the output console
     * @param displays a number of the table display abstractions
     */
    public SQLCommandHandler(final DatabaseDescriptor database, final OutputConsole console, final TableDisplay ...displays) {
        this.outputConsole = console;
        this.tableDisplays = displays;
        this.databaseDescriptor = database;
        final MiniMiserDatabase miniMiserDatabase = (MiniMiserDatabase) databaseDescriptor.getAttribute(AttributeIdentifier.Database);
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
                    case Count: return handleCount(command);
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
     * Mini Template Method [GoF] for "doing JDBC stuff" and having exceptions
     * handled in a common manner, suitable for the SQL tab.
     * 
     * @author matt
     *
     */
    private abstract class StatementTemplate {
        private Statement statement;
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

    private boolean handleCount(final String command) {
        return new StatementTemplate() {

            @Override
            public boolean performExecution() throws SQLException {
                final int count = getStatement().executeUpdate(command);
                outputConsole.info(count + " " + StringUtils.pluralise("row", count) + " updated");
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

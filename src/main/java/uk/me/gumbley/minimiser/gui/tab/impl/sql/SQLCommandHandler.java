    package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.gui.console.output.OutputConsole;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.sql.BadSQLException;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccess;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccessException;

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
    private TableDisplay tableDisplay;

    /**
     * @param console the output console
     * @param tabDisp the table display abstraction
     * @param database the database to use the SQL commands against
     */
    public SQLCommandHandler(final OutputConsole console, final TableDisplay tabDisp, final DatabaseDescriptor database) {
        this.outputConsole = console;
        this.tableDisplay = tabDisp;
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
                outputConsole.info("The result type is " + type);
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

    private boolean handleResultSet(final String command) {
        final Statement statement = sqlAccess.createStatement();
        try {
            final ResultSet resultSet = statement.executeQuery(command);
            final ResultSetMetaData metaData = resultSet.getMetaData();
            tableDisplay.setHeadings(getHeadingsFromMetaData(metaData));
            if (resultSet.first()) {
                do {
                    tableDisplay.addRow(stringifyResultSet(resultSet, metaData));
                } while (resultSet.next());
                tableDisplay.finished();
            } else {
                outputConsole.info("No rows returned");
            }
            return true;
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

    private boolean handleSuccessFailure(final String command) {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean handleCount(final String command) {
        // TODO Auto-generated method stub
        return false;
    }
}

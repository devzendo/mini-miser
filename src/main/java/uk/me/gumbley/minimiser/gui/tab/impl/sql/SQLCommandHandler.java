package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * A CommandHandler for SQL commands.
 * 
 * @author matt
 *
 */
public final class SQLCommandHandler implements CommandHandler {
    private final DatabaseDescriptor databaseDescriptor;

    /**
     * @param database the database to use the SQL commands against
     */
    public SQLCommandHandler(final DatabaseDescriptor database) {
        this.databaseDescriptor = database;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handleCommand(final String command) {
        // TODO Auto-generated method stub
        return false;
    }
}

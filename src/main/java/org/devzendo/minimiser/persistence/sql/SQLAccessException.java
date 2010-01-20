package org.devzendo.minimiser.persistence.sql;

import java.sql.SQLException;

/**
 * Thrown by the SQLAccess subsystem, or by the getSQLAccess method when
 * the SQLAccess subsystem is at fault.
 * @author matt
 *
 */
public class SQLAccessException extends RuntimeException {
    private static final long serialVersionUID = 2756943237189928339L;

    /**
     * Construct an exception with a message and a cause
     * @param message the message
     * @param cause the causing exception
     */
    public SQLAccessException(final String message, final SQLException cause) {
        super(message, cause);
    }

    /**
     * Construct an exception with just a message
     * @param message the message
     */
    public SQLAccessException(final String message) {
        super(message);
    }
}

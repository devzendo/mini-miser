package org.devzendo.minimiser.persistence.sql;

import java.sql.SQLException;

/**
 * Thrown by the SQLAccess parse method when the supplied SQL is malformed.
 * @author matt
 *
 */
public final class BadSQLException extends SQLAccessException {
    private static final long serialVersionUID = 7811380206297578786L;

    /**
     * Construct an exception with a message and a cause
     * @param message the message
     * @param cause the causing exception
     */
    public BadSQLException(final String message, final SQLException cause) {
        super(message, cause);
    }
}

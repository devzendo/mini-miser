package uk.me.gumbley.minimiser.persistence;

/**
 * A BadPasswordException is thrown when an attempt is made to access
 * an encrypted database for migration but the wrong password is
 * supplied.
 * 
 * TODO find out what Spring's JdbcTemplate throws in this case.
 * @author matt
 *
 */
public class BadPasswordException extends RuntimeException {
    private static final long serialVersionUID = -4237763396785087320L;
}

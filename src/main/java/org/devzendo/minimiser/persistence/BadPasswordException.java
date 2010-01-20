package org.devzendo.minimiser.persistence;

/**
 * A BadPasswordException is thrown when an attempt is made to access
 * an encrypted database for migration but the wrong password is
 * supplied.
 * 
 * @author matt
 *
 */
public class BadPasswordException extends RuntimeException {
    private static final long serialVersionUID = -3378063389975036678L;

    /**
     * Couldn't open database due to bad password
     * @param message helpful text
     */
    public BadPasswordException(final String message) {
        super(message);
    }

}

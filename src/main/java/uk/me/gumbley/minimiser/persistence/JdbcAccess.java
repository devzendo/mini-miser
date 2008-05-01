package uk.me.gumbley.minimiser.persistence;

public interface JdbcAccess {

    MigratableDatabase openDatabase(String databasePath, String password) throws BadPasswordException;
}

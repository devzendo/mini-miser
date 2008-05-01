package uk.me.gumbley.minimiser.persistence;

public interface AccessFactory {

    MigratableDatabase openMigratableDatabase(String directory, String password) throws BadPasswordException;
}

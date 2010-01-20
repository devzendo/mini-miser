package org.devzendo.minimiser.openlist;

/**
 * Something has happened to a specific database, this is the notification you
 * receive from the OpenDatabaseList, if you're an observer.
 * 
 * @author matt
 *
 */
public abstract class DatabaseDescriptorEvent extends DatabaseEvent {
    private final DatabaseDescriptor databaseDescriptor;

    /**
     * Create a DatabaseEvent describing a specific database.
     * @param descriptor the database descriptor of the database that
     * has opened.
     */
    public DatabaseDescriptorEvent(final DatabaseDescriptor descriptor) {
        super();
        this.databaseDescriptor = descriptor;
    }

    /**
     * Obtain the database descriptor 
     * @return the database descriptor
     */
    public final String getDatabaseName() {
        return databaseDescriptor.getDatabaseName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return databaseDescriptor.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final DatabaseDescriptorEvent other = (DatabaseDescriptorEvent) obj;
        if (databaseDescriptor == null) {
            if (other.databaseDescriptor != null) {
                return false;
            }
        } else if (!databaseDescriptor.equals(other.databaseDescriptor)) {
            return false;
        }
        return true;
    }

    /**
     * Obtain the DatabaseDescriptor associated with this event.
     * @return the DatabaseDescriptor
     */
    public final DatabaseDescriptor getDatabaseDescriptor() {
        return databaseDescriptor;
    }
}

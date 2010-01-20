package org.devzendo.minimiser.wiring.databaseeventlistener;

import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import org.junit.Assert;
import org.junit.Test;



/**
 * An Empty ApplicationMenu is populated in the DatabaseDescriptor on
 * database open notification by the OpenDatabaseList.
 *
 * @author matt
 *
 */
public final class TestApplicationMenuCreatingDatabaseEventListener {
    private static final String DATABASE = "db";

    /**
     *
     */
    @Test
    public void emptyApplicationMenuIsAddedToDatabaseDescriptorOnDatabaseOpen() {
        final OpenDatabaseList openDatabaseList = new OpenDatabaseList();

        final ApplicationMenuCreatingDatabaseEventListener adapter = new ApplicationMenuCreatingDatabaseEventListener();
        openDatabaseList.addDatabaseEventObserver(adapter);

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        Assert.assertNull(databaseDescriptor.getAttribute(AttributeIdentifier.ApplicationMenu));
        openDatabaseList.addOpenedDatabase(databaseDescriptor);

        Assert.assertNotNull(databaseDescriptor.getAttribute(AttributeIdentifier.ApplicationMenu));
    }
}

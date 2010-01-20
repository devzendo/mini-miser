package org.devzendo.minimiser.openlist;

import java.io.IOException;

import org.devzendo.minimiser.springloader.ApplicationContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the DatabaseDescriptor FactoryBean
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/openlist/DatabaseDescriptorFactoryTestCase.xml")
public final class TestDatabaseDescriptorFactory extends AbstractDatabaseDescriptorFactoryUnittestCase {

    /**
     * @throws IOException on failure
     *
     */
    @Test
    public void testStoreAndClearDescriptor() throws IOException {
        final DatabaseDescriptor dd = new DatabaseDescriptor("dd");
        Assert.assertNull(getDatabaseDescriptor());

        getDatabaseDescriptorFactory().setDatabaseDescriptor(dd);

        final DatabaseDescriptor dd2 = getDatabaseDescriptor();
        Assert.assertNotNull(dd2);

        Assert.assertEquals(dd.getDatabaseName(), dd2.getDatabaseName());
        Assert.assertSame(dd, dd2);

        getDatabaseDescriptorFactory().clearDatabaseDescriptor();
        Assert.assertNull(getDatabaseDescriptor());
    }

    /**
     * @throws IOException on failure
     */
    @Test
    public void itsNotASingleton() throws IOException {
        final DatabaseDescriptor dd1 = new DatabaseDescriptor("dd1");

        getDatabaseDescriptorFactory().setDatabaseDescriptor(dd1);

        final DatabaseDescriptor dd2 = new DatabaseDescriptor("dd2");

        getDatabaseDescriptorFactory().setDatabaseDescriptor(dd2);

        final DatabaseDescriptor ddcurrent = getDatabaseDescriptor();
        Assert.assertNotNull(ddcurrent);

        Assert.assertFalse(ddcurrent.getDatabaseName().equals(dd1.getDatabaseName()));
        Assert.assertNotSame(ddcurrent, dd1);
        Assert.assertSame(ddcurrent, dd2);
    }
}

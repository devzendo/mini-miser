package uk.me.gumbley.minimiser.openlist;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;

/**
 * Test the DatabaseDescriptor FactoryBean
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/openlist/DatabaseDescriptorFactoryTestCase.xml")
public final class TestDatabaseDescriptorFactory extends SpringLoaderUnittestCase {

    private DatabaseDescriptorFactory databaseDescriptorFactory;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        databaseDescriptorFactory = getDatabaseDescriptorFactory();
        Assert.assertNotNull(databaseDescriptorFactory);
    }
    
    /**
     * @throws IOException on failure
     * 
     */
    @Test
    public void testStoreAndClearDescriptor() throws IOException {
        final DatabaseDescriptor dd = new DatabaseDescriptor("dd");
        Assert.assertNull(getDatabaseDescriptor());

        databaseDescriptorFactory.setDatabaseDescriptor(dd);
        
        final DatabaseDescriptor dd2 = getDatabaseDescriptor();
        Assert.assertNotNull(dd2);
        
        Assert.assertEquals(dd.getDatabaseName(), dd2.getDatabaseName());
        Assert.assertSame(dd, dd2);
        
        databaseDescriptorFactory.clearDatabaseDescriptor();
        Assert.assertNull(getDatabaseDescriptor());
    }
    
    /**
     * @throws IOException on failure
     */
    @Test
    public void itsNotASingleton() throws IOException {
        final DatabaseDescriptor dd1 = new DatabaseDescriptor("dd1");

        databaseDescriptorFactory.setDatabaseDescriptor(dd1);

        final DatabaseDescriptor dd2 = new DatabaseDescriptor("dd2");

        databaseDescriptorFactory.setDatabaseDescriptor(dd2);

        final DatabaseDescriptor ddcurrent = getDatabaseDescriptor();
        Assert.assertNotNull(ddcurrent);

        Assert.assertFalse(ddcurrent.getDatabaseName().equals(dd1.getDatabaseName()));
        Assert.assertNotSame(ddcurrent, dd1);
        Assert.assertSame(ddcurrent, dd2);
    }

    private DatabaseDescriptor getDatabaseDescriptor() {
        return getSpringLoader().getBean("databaseDescriptor", DatabaseDescriptor.class);
    }

    private DatabaseDescriptorFactory getDatabaseDescriptorFactory() {
        return getSpringLoader().getBean("&databaseDescriptor", DatabaseDescriptorFactory.class);
    }
}

package uk.me.gumbley.minimiser.openlist;

import org.junit.Assert;
import org.junit.Before;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;

/**
 * Provides access to the configured DatabaseDescriptor FactoryBean and its
 * stored DatabaseDescriptor.
 * 
 * @author matt
 *
 */
public abstract class AbstractDatabaseDescriptorFactoryUnittestCase extends SpringLoaderUnittestCase {

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
     * Get the database descriptor that's stored in the DatabaseDescriptorFactory
     * @return the currently stored database descriptor.
     */
    protected DatabaseDescriptor getDatabaseDescriptor() {
        return getSpringLoader().getBean("databaseDescriptor", DatabaseDescriptor.class);
    }

    /**
     * Get the DatabaseDescriptorFactory
     * @return the database descriptor factory 
     */
    protected DatabaseDescriptorFactory getDatabaseDescriptorFactory() {
        return getSpringLoader().getBean("&databaseDescriptor", DatabaseDescriptorFactory.class);
    }
}

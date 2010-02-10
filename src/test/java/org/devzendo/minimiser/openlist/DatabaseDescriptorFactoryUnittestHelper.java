package org.devzendo.minimiser.openlist;

import org.devzendo.minimiser.springloader.SpringLoader;
import org.junit.Assert;

/**
 * Provides access to the configured DatabaseDescriptor FactoryBean and its
 * stored DatabaseDescriptor.
 *
 * @author matt
 *
 */
public final class DatabaseDescriptorFactoryUnittestHelper {

    private final SpringLoader mSpringLoader;
    private final DatabaseDescriptorFactory databaseDescriptorFactory;

    /**
     * @param springLoader the SpringLoader
     */
    public DatabaseDescriptorFactoryUnittestHelper(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
        databaseDescriptorFactory = getDatabaseDescriptorFactory();
        Assert.assertNotNull(databaseDescriptorFactory);
    }

    /**
     * Get the database descriptor that's stored in the DatabaseDescriptorFactory
     * @return the currently stored database descriptor.
     */
    public DatabaseDescriptor getDatabaseDescriptor() {
        return mSpringLoader.getBean("databaseDescriptor", DatabaseDescriptor.class);
    }

    /**
     * Get the DatabaseDescriptorFactory
     * @return the database descriptor factory
     */
    public DatabaseDescriptorFactory getDatabaseDescriptorFactory() {
        return mSpringLoader.getBean("&databaseDescriptor", DatabaseDescriptorFactory.class);
    }
}

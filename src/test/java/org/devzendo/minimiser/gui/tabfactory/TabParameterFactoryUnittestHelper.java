package org.devzendo.minimiser.gui.tabfactory;

import org.devzendo.minimiser.gui.tab.TabParameter;
import org.devzendo.minimiser.springloader.SpringLoader;
import org.junit.Assert;

/**
 * Provides access to the configured TabParameter FactoryBean and its
 * stored TabParameter.
 *
 * @author matt
 *
 */
public final class TabParameterFactoryUnittestHelper {

    private final SpringLoader mSpringLoader;
    private final TabParameterFactory mTabParameterFactory;

    /**
     * @param springLoader the SpringLoader
     */
    public TabParameterFactoryUnittestHelper(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
        mTabParameterFactory = getTabParameterFactory();
        Assert.assertNotNull(mTabParameterFactory);
    }

    /**
     * Get the tab parameter that's stored in the TabParameterFactory
     * @return the currently stored tab parameter.
     */
    public TabParameter getTabParameter() {
        return mSpringLoader.getBean("tabParameter", TabParameter.class);
    }

    /**
     * Get the TabParameterFactory
     * @return the tab parameter factory
     */
    public TabParameterFactory getTabParameterFactory() {
        return mSpringLoader.getBean("&tabParameter", TabParameterFactory.class);
    }
}

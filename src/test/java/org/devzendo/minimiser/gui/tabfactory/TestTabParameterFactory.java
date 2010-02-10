package org.devzendo.minimiser.gui.tabfactory;

import java.io.IOException;

import org.devzendo.minimiser.gui.tab.TabParameter;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the TabParameter FactoryBean
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/gui/tabfactory/TabParameterFactoryTestCase.xml")
public final class TestTabParameterFactory extends SpringLoaderUnittestCase {
    private TabParameterFactoryUnittestHelper mTabParameterFactoryHelper;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mTabParameterFactoryHelper = new TabParameterFactoryUnittestHelper(getSpringLoader());
    }

    /**
     * @throws IOException on failure
     *
     */
    @Test
    public void testStoreAndClearParameter() throws IOException {
        final TabParameter tabParameter = new TabParameter() {
        };
        Assert.assertNull(mTabParameterFactoryHelper.getTabParameter());

        mTabParameterFactoryHelper.getTabParameterFactory().setTabParameter(tabParameter);

        final TabParameter tp2 = mTabParameterFactoryHelper.getTabParameter();
        Assert.assertNotNull(tp2);

        Assert.assertSame(tabParameter, tp2);

        mTabParameterFactoryHelper.getTabParameterFactory().clearTabParameter();
        Assert.assertNull(mTabParameterFactoryHelper.getTabParameter());
    }

    /**
     * @throws IOException on failure
     */
    @Test
    public void itsNotASingleton() throws IOException {
        final TabParameter tp1 = new TabParameter() {
        };

        mTabParameterFactoryHelper.getTabParameterFactory().setTabParameter(tp1);

        final TabParameter tp2 = new TabParameter() {
        };

        mTabParameterFactoryHelper.getTabParameterFactory().setTabParameter(tp2);

        final TabParameter tpcurrent = mTabParameterFactoryHelper.getTabParameter();
        Assert.assertNotNull(tpcurrent);

        Assert.assertNotSame(tpcurrent, tp1);
        Assert.assertSame(tpcurrent, tp2);
    }
}

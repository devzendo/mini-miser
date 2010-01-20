package org.devzendo.minimiser.gui;

import java.io.IOException;

import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the MainFrameTitle FactoryBean
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/gui/MainFrameTitleFactoryTestCase.xml")
public final class TestMainFrameTitleFactory extends SpringLoaderUnittestCase {

    /**
     *
     */
    @Test
    public void testStoreMainFrameTitle() {
        final MainFrameTitle testTitle = new StubMainFrameTitle();

        final MainFrameTitleFactory prefsFactory = getMainFrameTitleFactory();
        Assert.assertNotNull(prefsFactory);
        prefsFactory.setMainFrameTitle(testTitle);

        final MainFrameTitle prefs = getMainFrameTitle();
        Assert.assertNotNull(prefs);

        Assert.assertEquals(testTitle, prefs);
    }

    /**
     * @throws IOException on failure
     */
    @Test
    public void itsASingleton() throws IOException {
        final MainFrameTitle testFrame = new StubMainFrameTitle();
        getMainFrameTitleFactory().setMainFrameTitle(testFrame);

        final MainFrameTitle frame1 = getMainFrameTitle();
        final MainFrameTitle frame2 = getMainFrameTitle();
        Assert.assertSame(frame1, frame2);
    }

    private MainFrameTitle getMainFrameTitle() {
        return getSpringLoader().getBean("mainFrameTitle", MainFrameTitle.class);
    }

    private MainFrameTitleFactory getMainFrameTitleFactory() {
        return getSpringLoader().getBean("&mainFrameTitle", MainFrameTitleFactory.class);
    }
}

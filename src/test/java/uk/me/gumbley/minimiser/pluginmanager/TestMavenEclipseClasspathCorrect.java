package uk.me.gumbley.minimiser.pluginmanager;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.junit.Assert;
import org.junit.Test;


/**
 * Do we have resources available once under the classpath?
 * @author matt
 *
 */
public class TestMavenEclipseClasspathCorrect {
    /**
     * @throws IOException
     */
    @Test
    public void shouldOnlySeeResourcesOnceInEnumeration() throws IOException {   
    final Enumeration<URL> urls =
        Thread.currentThread().getContextClassLoader().
        getResources("uk/me/gumbley/minimiser/pluginmanager/goodplugin.properties");
        int urlsize = 0;
        while (urls.hasMoreElements()) {
            urls.nextElement();
            urlsize++;
        }
        Assert.assertEquals("src/main/resources is available via " + urlsize
            + " classpath mounts - this will cause plugin loading failure",
            1, urlsize);
    }
}

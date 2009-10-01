package uk.me.gumbley.minimiser.pluginmanager;

import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.plugin.ApplicationPlugin;


/**
 * Tests the correct behaviour of the PluginUnittestCase,
 * by manually making the calls that JUnit would make.
 * Can't subclass it directly in here, 'cos its @Before
 * will throw before we get started, so we can't test for
 * its validation code.
 * 
 * @author matt
 *
 */
public final class TestPluginUnittestCaseBaseClass  {
    
    private static final class NoPluginsDeclaredSubclass extends PluginUnittestCase {
        
    }
    
    /**
     * no annotations are invalid
     */
    @Test(expected = AssertionError.class)
    public void noDeclaredPluginsCauseThrow() {
        final NoPluginsDeclaredSubclass subClass = new NoPluginsDeclaredSubclass();
        subClass.initialisePlugins(); // called by JUnit via @Before
    }


    @PluginUnderTest("uk.me.gumbley.minimiser.plugin.AppPlugin")
    private static final class PluginDeclaredSubclass extends
    PluginUnittestCase {
    }

    /**
     * 
     */
    @Test
    public void declaredPluginIsLoadedCorrectly() {
        final PluginDeclaredSubclass subClass = new PluginDeclaredSubclass();
        subClass.initialisePlugins(); // called by JUnit via @Before
        final ApplicationPlugin applicationPlugin = subClass.getApplicationPlugin();
        Assert.assertNotNull(applicationPlugin);
        // further tests - i.e. getSpringLoader() don't work - we
        // need a proper test class loaded with @Before
    }
}

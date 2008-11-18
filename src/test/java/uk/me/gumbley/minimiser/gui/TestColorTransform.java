package uk.me.gumbley.minimiser.gui;

import java.awt.Color;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Tests the ColorTransform
 * @author matt
 *
 */
public final class TestColorTransform extends LoggingTestCase {
    private ColorTransform transform;
    
    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        transform = new ColorTransform(Color.GRAY, Color.RED);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeIllegal() {
        transform.getProportionalColor((float) -0.1);
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void moreThanOneIllegal() {
        transform.getProportionalColor((float) 1.001);
    }

    /**
     * 
     */
    @Test
    public void startColorAt0() {
        final Color color = transform.getProportionalColor(0.0);
        Assert.assertEquals(Color.GRAY.getRed(), color.getRed());
        Assert.assertEquals(Color.GRAY.getGreen(), color.getGreen());
        Assert.assertEquals(Color.GRAY.getBlue(), color.getBlue());
        Assert.assertEquals(Color.GRAY, color);
    }
    
    /**
     * 
     */
    @Test
    public void endColorAt1() {
        final Color color = transform.getProportionalColor(1.0);
        Assert.assertEquals(Color.RED.getRed(), color.getRed());
        Assert.assertEquals(Color.RED.getGreen(), color.getGreen());
        Assert.assertEquals(Color.RED.getBlue(), color.getBlue());
        Assert.assertEquals(Color.RED, color);
    }
    

    /**
     * 
     */
    @Test
    public void mixedColorInbetween() {
        final Color color = transform.getProportionalColor(0.5);
        Assert.assertEquals(192, color.getRed());
        Assert.assertEquals(64, color.getGreen());
        Assert.assertEquals(64, color.getBlue());
    }
}

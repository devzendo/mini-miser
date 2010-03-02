/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.gui;

import java.awt.Color;

import org.devzendo.minimiser.logging.LoggingTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


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

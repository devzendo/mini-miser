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

package org.devzendo.minimiser.tabledisplay;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the Cell class
 * 
 * @author matt
 *
 */
public final class TestCell {
    private Cell cell;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        cell = new Cell();
    }
    
    /**
     * 
     */
    @Test
    public void dimensionsOfSimpleTextCellAreCorrect() {
        cell.setText("hello world");
        Assert.assertEquals(1, cell.getHeight());
        Assert.assertEquals(11, cell.getWidth());
    }
    
    /**
     * 
     */
    @Test
    public void dimensionsOfMultiLineTextCellAreCorrect() {
        cell.setText("hello world\nthis is a very long line");
        Assert.assertEquals(2, cell.getHeight());
        Assert.assertEquals(24, cell.getWidth());
    }
    
    /**
     * 
     */
    @Test
    public void paddedLinesAreCorrect() {
        cell.setText("hello world\nthis is a very long line");
        Assert.assertEquals("hello world             ", cell.getPaddedLine(0));
        Assert.assertEquals("this is a very long line", cell.getPaddedLine(1));
    }
    
    /**
     * 
     */
    @Test
    public void paddedLinesOutOfRangeAreEmpty() {
        cell.setText("hello world\nthis is a very long line");
        Assert.assertEquals("                        ", cell.getPaddedLine(16));
    }
    
    /**
     * 
     */
    @Test
    public void bannerLinesAreCorrect() {
        cell.setText       ("one");
        Assert.assertEquals("---+", cell.getBannerLine());
        
        cell.setText("hello world\nthis is a very long line");
        Assert.assertEquals      ("------------------------+", cell.getBannerLine());
    }
    
    /**
     * 
     */
    @Test
    public void setWidthCanChangeWidth() {
        cell.setText("hello world");
        
        cell.setWidth(20);
        Assert.assertEquals(20, cell.getWidth());
        Assert.assertEquals("hello world         ", cell.getPaddedLine(0));
        Assert.assertEquals("--------------------+", cell.getBannerLine());
        
        cell.setWidth(5);
        Assert.assertEquals(5, cell.getWidth());
        Assert.assertEquals("hello", cell.getPaddedLine(0));
        Assert.assertEquals("-----+", cell.getBannerLine());
        
        cell.setWidth(11);
        Assert.assertEquals(11, cell.getWidth());
        Assert.assertEquals("hello world", cell.getPaddedLine(0));
        Assert.assertEquals("-----------+", cell.getBannerLine());
        
    }
}

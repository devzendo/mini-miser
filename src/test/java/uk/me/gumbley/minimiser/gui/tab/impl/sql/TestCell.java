package uk.me.gumbley.minimiser.gui.tab.impl.sql;

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
        cell.setText("hello world\nthis is a very long line");
        Assert.assertEquals("-----------------------+", cell.getBannerLine());
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
        
        cell.setWidth(5);
        Assert.assertEquals(5, cell.getWidth());
        Assert.assertEquals("hello", cell.getPaddedLine(0));
        
        cell.setWidth(11);
        Assert.assertEquals(11, cell.getWidth());
        Assert.assertEquals("hello world", cell.getPaddedLine(0));
        
    }
}

package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the Row abstraction
 * 
 * @author matt
 *
 */
public final class TestRow {
    
    private Row row;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        row = new Row();
    }
    
    /**
     * 
     */
    @Test
    public void emptyRowHasNoHeight() {
        Assert.assertEquals(0, row.getHeight());

        Assert.assertEquals(0, row.size());
    }
    
    /**
     * 
     */
    @Test
    public void rowWithSimpleCellHasSingleRowHeight() {
        final Cell cell = new Cell();
        cell.setText("hello");
        row.addCell(cell);
        Assert.assertEquals(1, row.getHeight());
        
        Assert.assertEquals(1, row.size());
    }
    
    /**
     * 
     */
    @Test
    public void rowWithMultiLineCellsIsTallEnough() {
        final Cell singleHeightCell = new Cell();
        singleHeightCell.setText("hello");
        row.addCell(singleHeightCell);
        
        final Cell multiLineCell = new Cell();
        multiLineCell.setText("there are\nthree lines\nin this cell");
        row.addCell(multiLineCell);
        
        Assert.assertEquals(3, row.getHeight());

        Assert.assertEquals(2, row.size());
        
        Assert.assertSame(singleHeightCell, row.get(0));
        Assert.assertSame(multiLineCell, row.get(1));
        
        Assert.assertEquals(2, row.allCells().size());
    }
}

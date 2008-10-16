package uk.me.gumbley.minimiser.tabledisplay;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the TableDisplay.
 * 
 * @author matt
 *
 */
public final class TestTableDisplay {
    
    private StubTableDisplay tableDisplay;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        tableDisplay = new StubTableDisplay();
    }
    
    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void cantAddRowWithoutPriorHeading() {
        final List<String> row = new ArrayList<String>();
        row.add("sample column");
        tableDisplay.addRow(row);
    }
    
    /**
     * 
     */
    @Test
    public void setHeadingDoesntEmitHeadingUntilFinishedIfNoRowsAdded() {
        Assert.assertEquals(0, tableDisplay.getHeadingEmitCount());

        final List<String> headings = new ArrayList<String>();
        headings.add("Foo!");
        tableDisplay.setHeadings(headings);
        
        Assert.assertEquals(4, tableDisplay.getColumnWidth(0));

        Assert.assertEquals(0, tableDisplay.getHeadingEmitCount());
        
        tableDisplay.finished();
        Assert.assertEquals(1, tableDisplay.getHeadingEmitCount());
    }

    /**
     * 
     */
    @Test
    public void setHeadingDoesntEmitHeadingWhenFinishedIfRowsAdded() {
        Assert.assertEquals(0, tableDisplay.getHeadingEmitCount());

        final List<String> headings = new ArrayList<String>();
        headings.add("Foo!");
        tableDisplay.setHeadings(headings);
        
        Assert.assertEquals(4, tableDisplay.getColumnWidth(0));

        final List<String> row = new ArrayList<String>();
        row.add("a");
        tableDisplay.addRow(row);

        Assert.assertEquals(1, tableDisplay.getHeadingEmitCount());
        
        tableDisplay.finished();
        Assert.assertEquals(1, tableDisplay.getHeadingEmitCount());
    }

    /**
     * 
     */
    @Test
    public void showFirstRowEmitsRow() {
        final List<String> headings = new ArrayList<String>();
        headings.add("Foo!");
        tableDisplay.setHeadings(headings);
        
        final List<String> row = new ArrayList<String>();
        row.add("sample column");
        tableDisplay.addRow(row);
        
        Assert.assertEquals(1, tableDisplay.getRowEmitCount());
    }

    /**
     * 
     */
    @Test
    public void longColumnContentsEmitLongerHeader() {
        final List<String> headings = new ArrayList<String>();
        headings.add("Foo!");
        tableDisplay.setHeadings(headings);
        
        final List<String> row = new ArrayList<String>();
        row.add("sample column");
        tableDisplay.addRow(row);
        Assert.assertEquals(13, tableDisplay.getColumnWidth(0));
        
        Assert.assertEquals(1, tableDisplay.getHeadingEmitCount());
    }

    /**
     * 
     */
    @Test
    public void multiLineColumnContentsEmitsHeaderAsWideAsTheLongestLine() {
        final List<String> headings = new ArrayList<String>();
        headings.add("Foo!");
        tableDisplay.setHeadings(headings);
        
        final List<String> row = new ArrayList<String>();
        row.add("extraneously\none\ntwo\nthree\n");
        tableDisplay.addRow(row);
        Assert.assertEquals(12, tableDisplay.getColumnWidth(0));
        
        Assert.assertEquals(1, tableDisplay.getHeadingEmitCount());
    }
}

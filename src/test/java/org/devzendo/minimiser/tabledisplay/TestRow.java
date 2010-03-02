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

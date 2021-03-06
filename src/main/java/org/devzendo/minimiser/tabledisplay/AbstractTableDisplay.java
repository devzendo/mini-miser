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

import java.util.ArrayList;
import java.util.List;

/**
 * Performs base functionality for all TableDisplays.
 * @author matt
 *
 */
public abstract class AbstractTableDisplay implements TableDisplay {

    private Row headings;
    private List<Integer> columnWidths;
    private boolean emitHeading;
    
    /**
     * Construct an Abstract Table Display
     */
    public AbstractTableDisplay() {
        headings = new Row();
        columnWidths = new ArrayList<Integer>();
    }

    /**
     * {@inheritDoc}
     */
    public final void setHeadings(final List<String> headingNames) {
        headings = new Row();
        columnWidths = new ArrayList<Integer>(headings.size());
        for (int i = 0; i < headingNames.size(); i++) {
            final String heading = headingNames.get(i);
            final Cell headingCell = new Cell();
            headingCell.setText(heading);
            headings.addCell(headingCell);
            columnWidths.add(heading.length());
            if (columnWidths.get(i) < headingCell.getWidth()) {
                columnWidths.set(i, headingCell.getWidth());
            } else {
                headingCell.setWidth(columnWidths.get(i));
            }
        }
        emitHeading = true;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void addRow(final List<String> row) {
        if (headings.size() == 0) {
            throw new IllegalStateException("No headings have been set");
        }
        
        final Row cells = new Row();
        for (int i = 0; i < row.size(); i++) {
            final Object colObject = row.get(i);
            final String colString = colObject == null ? "(null)" : colObject.toString();
            final Cell cell = new Cell();
            cell.setText(colString);
            cells.addCell(cell);
            if (columnWidths.get(i) < cell.getWidth()) {
                columnWidths.set(i, cell.getWidth());
                emitHeading = true;
            } else {
                cell.setWidth(columnWidths.get(i));
            }
            // keep the heading cell widths maximised
            headings.get(i).setWidth(columnWidths.get(i));
        }
        if (emitHeading) {
            emitHeading(headings);
            emitHeading = false;
        }
        emitRow(cells);
    }
    
    /**
     * Emit the heading names supplied. Subclasses should call 
     * getColumnWidth(column) to determine the width of the columns.
     * @param headingCells the cells containing the names of the headings
     */
    protected abstract void emitHeading(final Row headingCells);

    /**
     * Emit the row supplied. Subclasses should call getColumnWidth(column) to
     * determine the width of the columns.
     * @param rowCells the row contents
     */
    protected abstract void emitRow(final Row rowCells);
    
    /**
     * Finalise the display after finished has been called. 
     */
    protected abstract void finish();
    
    /**
     * {@inheritDoc}
     */
    public final int getColumnWidth(final int columnIndex) {
        return columnWidths.get(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    public final void finished() {
        if (emitHeading) {
            emitHeading(headings);
            emitHeading = false;
        }
        finish();
    }
}

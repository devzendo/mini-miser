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

import java.util.List;

/**
 * A tabular display that shows the column names of query results,
 * and each line of the results. Also computes the maximum width
 * needed to display each column of the table, given the widths of
 * the headings and cells in each column.
 * 
 * @author matt
 *
 */
public interface TableDisplay {
    
    /**
     * Set the heading names for the table display
     * @param headings a list of heading names
     */
    void setHeadings(List<String> headings);
    
    /**
     * Add a row of data to the table display. Throws IllegalStateException if
     * no headings have been set.
     * @param row the row of data.
     */
    void addRow(List<String> row);
    
    /**
     * How wide is this column, or how wide has it become after rows have been
     * displayed? Can throw ArrayIndexOutOfBoundsException if you exceed the
     * column count set by setHeadings.
     * @param columnIndex the index into the column list, starting at 0
     * @return the column width
     */
    int getColumnWidth(final int columnIndex);

    /**
     * This display is finished; pretty-up anything that needs prettying up.
     */
    void finished();
}

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

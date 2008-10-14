package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs base functionality for all TableDisplays.
 * @author matt
 *
 */
public abstract class AbstractTableDisplay implements TableDisplay {

    private List<String> headings;
    private List<Integer> columnWidths;
    private boolean emitHeading;
    
    /**
     * Construct an Abstract Table Display
     */
    public AbstractTableDisplay() {
        headings = new ArrayList<String>();
        columnWidths = new ArrayList<Integer>();
    }

    /**
     * {@inheritDoc}
     */
    public final void setHeadings(final List<String> headingNames) {
        headings = new ArrayList<String>(headingNames);
        columnWidths = new ArrayList<Integer>(headings.size());
        for (final String heading : headings) {
            columnWidths.add(heading.length());
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
        
        final List<Cell> cells = new ArrayList<Cell>();
        int maxHeight = 0;
        for (int i = 0; i < row.size(); i++) {
            final Object colObject = row.get(i);
            final String colString = colObject == null ? "(null)" : colObject.toString();
            final Cell cell = new Cell();
            cell.setText(colString);
            cells.add(cell);
            if (cell.getHeight() > maxHeight) {
                maxHeight = cell.getHeight();
            }
            if (columnWidths.get(i) < cell.getWidth()) {
                columnWidths.set(i, cell.getWidth());
                emitHeading = true;
            } else {
                cell.setWidth(columnWidths.get(i));
            }
        }
        if (emitHeading) {
            emitHeading(headings);
            emitHeading = false;
        }
        emitRow(cells);
    }
    
    /**
     * Obtain the width of a column, which in the case of multiline lines, is
     * the longest line within the column. In the case of single lines, it's the
     * length of the line as a whole.
     * @param columnText the text. Precondition: never null.
     * @return the width
     */
    private int columnWidth(final String columnText) {
        final String[] lines = columnText.split("\r?\n");
        int longestLength = 0;
        for (final String line : lines) {
            final int length = line.length();
            if (length > longestLength) {
                longestLength = length;
            }
        }
        return longestLength;
    }
    
    /**
     * Emit the heading names supplied. Subclasses should call 
     * getColumnWidth(column) to determine the width of the columns.
     * @param headingNames the names of the headings
     */
    protected abstract void emitHeading(final List<String> headingNames);

    /**
     * Emit the row supplied. Subclasses should call getColumnWidth(column) to
     * determine the width of the columns.
     * @param row the row contents
     */
    protected abstract void emitRow(final List<Cell> row);
    
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

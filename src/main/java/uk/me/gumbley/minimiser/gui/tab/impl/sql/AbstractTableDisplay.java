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
        emitHeading(headings);
        emitHeading = false;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void addRow(final List<Object> row) {
        if (headings.size() == 0) {
            throw new IllegalStateException("No headings have been set");
        }
        final List<String> colStrings = new ArrayList<String>();
        for (int i = 0; i < row.size(); i++) {
            final Object colObject = row.get(i);
            final String colString = colObject == null ? "(null)" : colObject.toString();
            if (columnWidths.get(i) < colString.length()) {
                columnWidths.set(i, colString.length());
                emitHeading = true;
            }
            colStrings.add(colString);
        }
        if (emitHeading) {
            emitHeading(headings);
            emitHeading = false;
        }
        emitRow(colStrings);
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
    protected abstract void emitRow(final List<String> row);
    
    /**
     * {@inheritDoc}
     */
    public final int getColumnWidth(final int columnIndex) {
        return columnWidths.get(columnIndex);
    }

}

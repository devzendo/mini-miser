package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import java.util.List;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.tabledisplay.TableDisplay;

/**
 * A TableDisplay that uses a ResultTable's JTable to render the
 * result data. Effectively an Adapter[GoF] between TableDisplay
 * and ResultTable, and also ensures Swing thread safety by making
 * all calls to the ResultTable on the EDT. 
 * 
 * @author matt
 *
 */
public final class TableTableDisplay implements TableDisplay {
    private final ResultTable resultTable;

    /**
     * Construct the TableTableDisplay given the ResultTable to use to output
     * @param table the output table
     */
    public TableTableDisplay(final ResultTable table) {
        this.resultTable = table;
    }

    /**
     * {@inheritDoc}
     */
    public void setHeadings(final List<String> headings) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                resultTable.setHeadings(headings);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void addRow(final List<String> row) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                resultTable.addRow(row);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void finished() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                resultTable.finished();
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    public int getColumnWidth(final int columnIndex) {
        // Used by tests for the Abstract / Stub / Console TableDisplay, the
        // JTable-based TableDisplay doesn't need to support this.
        return 0;
    }
}

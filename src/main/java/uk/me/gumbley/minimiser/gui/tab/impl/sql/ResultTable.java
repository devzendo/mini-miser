package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.nadeausoftware.ZebraJTable;


/**
 * Encapsulates the JTable used to display ResultSets via a TableDisplay.
 * 
 * @author matt
 */
public final class ResultTable {
    private final ResultTableModel dataModel;
    private final JScrollPane scrollPane;
    private final JTable mTable;
    
    /**
     * Construct the ResultTable
     */
    @SuppressWarnings("serial")
    public ResultTable() {
        dataModel = new ResultTableModel();
        mTable = new ZebraJTable(dataModel);
        scrollPane = new JScrollPane(mTable);
    }
    
    @SuppressWarnings("serial")
    private final class ResultTableModel extends AbstractTableModel {
        private final List<String> columnNames;
        private final List<List<String>> data;
        
        
        public ResultTableModel() {
            columnNames = new ArrayList<String>();
            data = new ArrayList<List<String>>();
        }
        
        public int getColumnCount() {
            return columnNames.size();
        }

        public int getRowCount() {
            return data.size();
        }

        public Object getValueAt(final int row, final int col) {
            if (row >= data.size()) {
                return "";
            }
            final List<String> rowList = data.get(row);
            if (rowList == null || col >= rowList.size()) {
                return "";
            }
            final String value = rowList.get(col);
            return value;
        }
        
        @Override
        public boolean isCellEditable(final int row, final int col) {
            return false;
        }
        
        @Override
        public String getColumnName(final int col) {
            return columnNames.get(col);
        }
        public void setColumnNames(final List<String> columns) {
            data.clear();
            columnNames.clear();
            columnNames.addAll(columns);
        }

        public void addRow(final List<String> row) {
            data.add(new ArrayList<String>(row));
        }
    };
    
    /**
     * Obtain the table for display
     * @return the table (in a scroll pane, in a panel)
     */
    public JComponent getTable() {
        return scrollPane;
    }

    /**
     * Set the headings in the table model
     * @param headings the headings
     */
    public void setHeadings(final List<String> headings) {
        dataModel.setColumnNames(headings);
    }
    
    /**
     * Add a row of data to the table model
     * @param row the row of data
     */
    public void addRow(final List<String> row) {
        dataModel.addRow(row);
    }
    
    /**
     * Trigger the table update
     */
    public void finished() {
        dataModel.fireTableStructureChanged();
    }
}

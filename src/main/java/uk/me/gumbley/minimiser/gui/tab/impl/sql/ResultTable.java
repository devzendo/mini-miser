package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * Encapsulates the JTable used to display ResultSets via a TableDisplay.
 * 
 * @author matt
 */
public final class ResultTable {
    private ResultTableModel dataModel;
    private JScrollPane scrollPane;
    
    /**
     * Construct the ResultTable
     */
    @SuppressWarnings("serial")
    public ResultTable() {
        dataModel = new ResultTableModel(); 
        scrollPane = new JScrollPane(new JTable(dataModel));
    }
    
    @SuppressWarnings("serial")
    private final class ResultTableModel extends AbstractTableModel {
        private List<String> columnNames;
        private List<List> data;
        
        
        public ResultTableModel() {
            columnNames = new ArrayList<String>();
            data = new ArrayList<List>();
        }
        
        public int getColumnCount() {
            return columnNames.size();
        }

        public int getRowCount() {
            return data.size();
        }

        @SuppressWarnings("unchecked")
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
        
        public boolean isCellEditable(final int row, final int col) {
            return false;
        }
        
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

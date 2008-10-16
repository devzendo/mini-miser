package uk.me.gumbley.minimiser.tabledisplay;

import java.util.ArrayList;
import java.util.List;

/**
 * A Row holds a list of Cells, and knows the height of the tallest.
 *  
 * @author matt
 *
 */
public final class Row {
    private int maxHeight = 0;
    private List<Cell> cells = new ArrayList<Cell>();
    
    /**
     * Obtain the height of the row, based on the tallest cell
     * @return the row height
     */
    public int getHeight() {
        return maxHeight;
    }

    /**
     * Add a cell to the row
     * @param cell the cell to add
     */
    public void addCell(final Cell cell) {
        if (cell.getHeight() > maxHeight) {
            maxHeight = cell.getHeight();
        }
        cells.add(cell);
    }

    /**
     * Obtain the size of the row (the number of cells in it)
     * @return the number of cells in the row
     */
    public int size() {
        return cells.size();
    }

    /**
     * Obtain the indexed cell
     * @param index the index into the cell list
     * @return the cell at index
     */
    public Cell get(final int index) {
        return cells.get(index);
    }
    
    /**
     * Get all the cells
     * @return the list of cells
     */
    public List<Cell> allCells() {
        return cells;
    }
}

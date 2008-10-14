package uk.me.gumbley.minimiser.gui.tab.impl.sql;

/**
 * A Cell holds the contents of a single cell in a display of rows (of data or
 * headings). It allows for multi-line text, and splits it accordingly to
 * allow the querying of width and height of the text.
 * 
 * @author matt
 *
 */
public final class Cell {

    private String cellText;
    private int height;
    private int width;
    private String[] lines;

    /**
     * Set the text of a cell
     * @param text the text
     */
    public void setText(final String text) {
        cellText = text == null ? "" : text;
        
        lines = cellText.split("\r?\n");
        height = lines.length;
        width = 0;
        for (final String line : lines) {
            final int length = line.length();
            if (length > width) {
                width = length;
            }
        }
    }

    /**
     * Obtain the height of the cell
     * @return the cell height in lines
     */
    public int getHeight() {
        return height;
    }

    /**
     * Obtain the width of the cell
     * @return the width in characters
     */
    public int getWidth() {
        return width;
    }

    /**
     * Obtain a cell line, left-justified, and padded with spaces to the cell
     * width
     * @param index the line index, starting at 0
     * @return the padded line
     */
    public String getPaddedLine(final int index) {
        final StringBuilder sb = new StringBuilder();
        final String line = index < lines.length ? lines[index] : "";
        final int restrictedWidth = Math.min(width, line.length());
        sb.append(line.substring(0, restrictedWidth));
        for (int i = restrictedWidth; i < width; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * Obtain a banner line of the correct width
     * @return ----+
     */
    public String getBannerLine() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width - 1; i++) {
            sb.append('-');
        }
        sb.append('+');
        return sb.toString();
    }

    /**
     * Change the width of the cell, usually to pad out since the table display
     * makes cells wider to accommodate wider contents in later rows, but does
     * not shrink cells.
     * @param newWidth the new width
     */
    public void setWidth(final int newWidth) {
        width = newWidth;
    }
}

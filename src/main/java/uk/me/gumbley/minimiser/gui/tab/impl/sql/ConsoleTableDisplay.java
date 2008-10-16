package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import uk.me.gumbley.minimiser.gui.console.output.OutputConsole;
import uk.me.gumbley.minimiser.tabledisplay.AbstractTableDisplay;
import uk.me.gumbley.minimiser.tabledisplay.Cell;
import uk.me.gumbley.minimiser.tabledisplay.Row;

/**
 * A table display that emits fancy headers and rows in ASCII boxes,
 * a kind of homage to MySQL.
 * @author matt
 *
 */
public final class ConsoleTableDisplay extends AbstractTableDisplay {
    private final OutputConsole console;
    private String plussesAndMinusses;
    
    /**
     * @param outputConsole the output console
     */
    public ConsoleTableDisplay(final OutputConsole outputConsole) {
        super();
        console = outputConsole;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void emitHeading(final Row headings) {
        final StringBuilder banner = new StringBuilder();
        banner.append('+');
        final StringBuilder header = new StringBuilder();
        header.append('|');
        int maxHeight = 0;
        for (final Cell cell : headings.allCells()) {
            if (cell.getHeight() > maxHeight) {
                maxHeight = cell.getHeight();
            }
        }
        for (int y = 0; y < maxHeight; y++) {
            for (final Cell cell : headings.allCells()) {
                banner.append(cell.getBannerLine());

                header.append(cell.getPaddedLine(y));
                header.append('|');
            }
        }
        plussesAndMinusses = banner.toString();
        final StringBuilder out = new StringBuilder();
        out.append(plussesAndMinusses);
        out.append("\n");
        out.append(header.toString());
        out.append("\n");
        out.append(plussesAndMinusses);
        console.info(out.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void emitRow(final Row row) {
        final StringBuilder out = new StringBuilder();
        for (int y = 0; y < row.getHeight(); y++) {
            out.append('|');
            for (final Cell cell : row.allCells()) {
                out.append(cell.getPaddedLine(y));
                out.append('|');
            }
            out.append("\n");
        }
        if (out.length() > 0) {
            console.info(out);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void finish() {
        console.info(plussesAndMinusses);
    }
}
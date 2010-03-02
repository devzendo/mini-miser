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

package org.devzendo.minimiser.gui.tab.impl.sql;

import org.devzendo.minimiser.gui.console.output.OutputConsole;
import org.devzendo.minimiser.tabledisplay.AbstractTableDisplay;
import org.devzendo.minimiser.tabledisplay.Cell;
import org.devzendo.minimiser.tabledisplay.Row;

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
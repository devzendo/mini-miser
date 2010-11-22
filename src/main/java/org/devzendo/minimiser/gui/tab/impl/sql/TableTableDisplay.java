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

import java.util.List;

import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.minimiser.tabledisplay.TableDisplay;


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

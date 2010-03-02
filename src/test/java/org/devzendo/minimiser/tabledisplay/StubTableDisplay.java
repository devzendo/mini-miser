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

package org.devzendo.minimiser.tabledisplay;




/**
 * A simple table display that allows the AbstractTableDisplay base functions
 * to be tested.
 * 
 * @author matt
 *
 */
public final class StubTableDisplay extends AbstractTableDisplay {
    private int headingEmitCount;
    private int rowEmitCount;
    
    /**
     * Constructor 
     */
    public StubTableDisplay() {
        super();
        headingEmitCount = 0;
        rowEmitCount = 0;
    }
    
    /**
     * @return howmany headings have been emitted
     */
    public int getHeadingEmitCount() {
        return headingEmitCount;
    }
    
    /**
     * @return how many rows have been emitted
     */
    public int getRowEmitCount() {
        return rowEmitCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void emitHeading(final Row headings) {
        headingEmitCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void emitRow(final Row row) {
        rowEmitCount++;
    }

    /**
     * {@inheritDoc}
     */
    public void finish() {
        // nothing
    }
}

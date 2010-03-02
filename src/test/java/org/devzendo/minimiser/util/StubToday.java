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

package org.devzendo.minimiser.util;



/**
 * A Today for use by unit tests to inject a specific value for today.
 * @author matt
 *
 */
public final class StubToday implements Today {
    private final String fixDate;
    
    /**
     * Create a Today that will return a fixed date 
     * @param fixedDateString a fixed date
     */
    public StubToday(final String fixedDateString) {
        fixDate = fixedDateString;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getUKDateString() {
        return fixDate;
    }
}

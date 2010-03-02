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

package org.devzendo.minimiser.migrator;

/**
 * An entity that this plugin creates upon migration; used
 * with SimpleJdbcTemplate, a la Version.
 * @author matt
 *
 */
public final class SampleObject {
    private final String mName;
    private final int mQuantity;

    /**
     * Construct the sample object bean
     * @param name the name
     * @param quantity the quantity
     */
    public SampleObject(final String name, final int quantity) {
        mName = name;
        mQuantity = quantity;
    }
    
    /**
     * @return the quantity
     */
    public int getQuantity() {
        return mQuantity;
    }

    /**
     * @return the name
     */
    public String getName() {
        return mName;
    }
}
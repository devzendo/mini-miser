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

package org.devzendo.minimiser.prefs;

/**
 * BooleanFlags are used to name the generalised Boolean Flag storage available
 * through Prefs.
 * <p>
 * They're effectively type-safe Strings. This is not an enum since other
 * plugins will probably want to contribute to the flags.
 * 
 * @author matt
 *
 */
public class BooleanFlag {
    private String flagName;
    
    /**
     * Construct a BooleanFlag given its name
     * @param name the name, which will be used as a key into the appropriate
     * section in Prefs
     */
    BooleanFlag(final String name) {
        flagName = name;
    }
    
    /**
     * @return the Boolean Flag's name
     */
    public final String getFlagName() {
        return flagName;
    }
    
    /**
     * {@inheritDoc}
     */
    public final String toString() {
        return flagName;
    }
}

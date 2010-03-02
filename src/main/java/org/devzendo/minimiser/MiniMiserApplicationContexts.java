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

package org.devzendo.minimiser;


/**
 * Holds the full set of standard application context files.
 *
 * @author matt
 *
 */
public final class MiniMiserApplicationContexts {
    /**
     * No instances
     */
    private MiniMiserApplicationContexts() {
        // nothing
    }

    /**
     * @return an array of standard application contexts used by
     * the framework.
     */
    public static String[] getApplicationContexts() {
        return new String[] {
                "org/devzendo/minimiser/MiniMiser.xml",
                "org/devzendo/minimiser/Menu.xml"
        };
    }
}

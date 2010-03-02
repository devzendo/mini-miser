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
 * The BooleanFlags used by the core application.
 * @author matt
 *
 */
public final class CoreBooleanFlags {
    private CoreBooleanFlags() {
        // no instances
    }
    /**
     * Is a check for update availability allowed?
     */
    public static final BooleanFlag UPDATE_CHECK_ALLOWED = new BooleanFlag("update_check_allowed");
}

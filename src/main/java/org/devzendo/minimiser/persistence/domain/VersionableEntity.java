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

package org.devzendo.minimiser.persistence.domain;

/**
 * Constants for the possible values of the Versions.Entity field.
 *
 * @author matt
 *
 */
public final class VersionableEntity {

    /**
     * don't construct
     */
    private VersionableEntity() {
        // no instances
    }

    /**
     * A Versions row with the entity SCHEMA_VERSION defines the
     * version of this plugin's database schema.
     *
     * The set of schema versions are ComparableVersions, which
     * can be monotonically increasing integers starting at 1.
     * They can have sub-versions, e.g. 1.0 < 1.1 < 1.2.
     */
    public static final String SCHEMA_VERSION = "schema";

    /**
     * A Versions row with the entity PLUGIN_CODE_VERSION defines
     * the version of the plugin code that created this database.
     *
     * The set of plugin versions are of the form x.y.z[-SNAPSHOT],
     * i.e. the version of the plugin artifact in the component
     * repository: ComparableVersions, e.g. 1.0 < 1.1 < 1.2.
     */
    public static final String PLUGIN_CODE_VERSION = "application";
}

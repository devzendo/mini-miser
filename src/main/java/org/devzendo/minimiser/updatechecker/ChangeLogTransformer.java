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

package org.devzendo.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.devzendo.minimiser.version.ComparableVersion;


/**
 * Transforms a change log file by taking the relevant sections - any changes
 * between latest version and the version you're running, and translates them
 * into an appropriate display form, e.g. HTML.
 * 
 * @author matt
 *
 */
public interface ChangeLogTransformer {

    /**
     * Scan through the change log file, finding the subsection that describes
     * changes between the current (runtime) version and the latest version
     * available from the update site. Transform the subsection into the
     * appropriate display form, e.g. HTML.
     * @param currentVersion the current runtime version
     * @param latestVersion the latest version available on the update site
     * @param changeLogFile the change log file as downloaded from the update
     * site
     * @return the transformed subsection
     * @throws IOException on file read or transformation failure
     * @throws ParseException on transformation failure
     */
    String readFileSubsection(
            ComparableVersion currentVersion,
            ComparableVersion latestVersion,
            File changeLogFile) throws IOException, ParseException;
    
    /**
     * Scan through the change log file. Transform all of it into
     * the appropriate display form, e.g. HTML.
     * @param changeLogFile the change log file as an InputStream
     * @return the transformed log
     * @throws IOException on file read or transformation failure
     * @throws ParseException on transformation failure
     */
    String readAllStream(
            InputStream changeLogFile) throws IOException, ParseException;
}

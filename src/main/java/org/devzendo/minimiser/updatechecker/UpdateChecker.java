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

/**
 * The UpdateChcker checks for updates to the software, asynchronously when
 * triggered.
 * 
 * @author matt
 *
 */
public interface UpdateChecker {

    /**
     * The name of the remote file that contains just the
     * latest version number.
     */
    String VERSION_NUMBER_FILE = "version.txt";
    
    /**
     * The name of the remote file that contains the change log.
     */
    String CHANGE_LOG_FILE = "changelog.txt";

    /**
     * Trigger an update check, providing feedback using the progress adapter
     * @param progressAdapter the UpdateProgressAdapter to use for feedback
     */
    void triggerUpdateCheck(UpdateProgressAdapter progressAdapter);
}

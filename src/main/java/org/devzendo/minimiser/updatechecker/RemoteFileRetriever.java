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

/**
 * A RemoteFileRetriever retrieves a named file from a given remote web server.
 * 
 * @author matt
 *
 */
public interface RemoteFileRetriever {

    /**
     * Download the named file, and return its entire contents in a String.
     * Take care with large files!
     * @param updateBaseURL the base URL of the update server
     * @param fileName the name of the file, relative to some base URL that
     * is separately configured
     * @return the file contents, which may be an empty string, but never null
     * @throws IOException on any comms failure
     */
    String getFileContents(String updateBaseURL, String fileName) throws IOException;

    /**
     * Download the named file and store it in a temporary file that's marked
     * for delete on exit.
     * @param updateBaseURL the base URL of the update server
     * @param fileName the name of the file, relative to some base URL that
     * is separately configured
     * @return the File, which contains the downloaded file contents
     * @throws IOException on any comms failure
     */
    File saveFileContents(String updateBaseURL, String fileName) throws IOException;

}

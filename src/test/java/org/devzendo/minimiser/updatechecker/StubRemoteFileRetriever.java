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
import java.io.PrintWriter;

/**
 * Allows tests to "remotely retrieve files".
 * 
 * @author matt
 *
 */
public final class StubRemoteFileRetriever implements RemoteFileRetriever {
    
    private IOException versionNumberRetrievalCommsFailureException;
    private IOException changeLogRetrievalCommsFailureException;
    private String version;
    private String changeLogContents;
    private String mUpdateURL;

    /**
     * 
     */
    public void injectCommsFailureOnVersionNumberRetrieval() {
        versionNumberRetrievalCommsFailureException = new IOException("Unable to retrieve remote data");
    }
    
    /**
     * 
     */
    public void injectCommsFailureOnChangeLogRetrieval() {
        changeLogRetrievalCommsFailureException = new IOException("Unable to retrieve remote data");
    }
    
    /**
     * @param v the injected version number
     */
    void injectReturnedVersionNumber(final String v) {
        version = v;
    }
    
    /**
     * @param contents the log contents
     */
    void injectReturnedChangeLogContents(final String contents) {
        changeLogContents = contents;
    }

    /**
     * {@inheritDoc}
     */
    public String getFileContents(final String updateBaseURL, final String fileName) throws IOException {
        mUpdateURL = updateBaseURL;
        if (versionNumberRetrievalCommsFailureException != null) {
            throw versionNumberRetrievalCommsFailureException;
        }
        return version;
    }

    /**
     * {@inheritDoc}
     */
    public File saveFileContents(final String updateBaseURL, final String fileName) throws IOException {
        mUpdateURL = updateBaseURL;
        if (changeLogRetrievalCommsFailureException != null) {
            throw changeLogRetrievalCommsFailureException;
        }
        
        final File file = File.createTempFile("minimiser-download", "dat");
        file.deleteOnExit();
        final PrintWriter pw = new PrintWriter(file);
        pw.print(changeLogContents);
        pw.close();
        
        return file;
    }

    /**
     * @return the update URL
     */
    public String getUpdateURL() {
        return mUpdateURL;
    }
}

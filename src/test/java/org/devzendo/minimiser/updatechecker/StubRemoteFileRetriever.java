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

package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import uk.me.gumbley.minimiser.common.AppName;

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

    void injectCommsFailureOnVersionNumberRetrieval() {
        versionNumberRetrievalCommsFailureException = new IOException("Unable to retrieve remote data");
    }
    
    public void injectCommsFailureOnChangeLogRetrieval() {
        changeLogRetrievalCommsFailureException = new IOException("Unable to retrieve remote data");
    }
    
    void injectReturnedVersionNumber(final String v) {
        version = v;
    }
    
    void injectReturnedChangeLogContents(final String contents) {
        changeLogContents = contents;
    }

    public String getFileContents(final String fileName) throws IOException {
        if (versionNumberRetrievalCommsFailureException != null) {
            throw versionNumberRetrievalCommsFailureException;
        }
        return version;
    }

    public File saveFileContents(final String fileName) throws IOException {
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

}

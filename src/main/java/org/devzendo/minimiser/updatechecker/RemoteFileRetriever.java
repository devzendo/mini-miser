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

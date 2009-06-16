package uk.me.gumbley.minimiser.updatechecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Tests that the remote file retriever can obtain files over HTTP.
 * 
 * @author matt
 *
 */
public final class TestRemoteFileRetriever extends LoggingTestCase {
    private static final String CHANGELOG_CONTENTS = "example text";
    private static final String CHANGELOG_TXT = "changelog.txt";
    private static final int PORT = 9876;
    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final Logger LOGGER = Logger
            .getLogger(TestRemoteFileRetriever.class);
    private RemoteFileRetriever remoteFileRetriever;
    private WebServer webServer;
    
    /**
     * @throws IOException hopefully never
     */
    @Before
    public void getPreRequisites() throws IOException {
        webServer = WebServer.createServer(PORT);
        webServer.serveFileContents(BASE_URL, CHANGELOG_TXT, CHANGELOG_CONTENTS);

        remoteFileRetriever = new DefaultRemoteFileRetriever();
    }
    
    /**
     * 
     */
    @After
    public void shutdownWebServer() {
        if (webServer != null) {
            webServer.stop();
        }
    }
    
    /**
     * @throws IOException not in this test
     */
    @Test(timeout = 8000)
    public void getChangelogOK() throws IOException {
        Assert.assertEquals(CHANGELOG_CONTENTS, remoteFileRetriever.getFileContents(BASE_URL, CHANGELOG_TXT));
    }

    /**
     * @throws IOException not in this test
     */
    @Test(timeout = 8000)
    public void saveChangelogOK() throws IOException {
        final File savedFile = remoteFileRetriever.saveFileContents(BASE_URL, CHANGELOG_TXT);
        Assert.assertNotNull(savedFile);
        try {
            Assert.assertTrue(savedFile.exists() && savedFile.isFile());
            final String contents = loadFile(savedFile);
            Assert.assertEquals(CHANGELOG_CONTENTS, contents);
        } finally {
            if (!savedFile.delete()) {
                LOGGER.warn("Could not delete temporary file " + savedFile.getAbsolutePath());
            }
        }
    }

    private String loadFile(final File savedFile) throws IOException {
        final StringBuffer sb = new StringBuffer();
        final BufferedReader br = new BufferedReader(new FileReader(savedFile));
        while (true) {
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}

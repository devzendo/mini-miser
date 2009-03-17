package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;


/**
 * Tests that the remote file retriever can obtain files over HTTP.
 * 
 * @author matt
 *
 */
public class TestRemoteFileRetriever extends LoggingTestCase {
    private static final String CHANGELOG_CONTENTS = "example text";
    private static final String CHANGELOG_TXT = "changelog.txt";
    private static final String BASE_URL = "http://localhost:9876";
    private static final Logger LOGGER = Logger
            .getLogger(TestRemoteFileRetriever.class);
    private RemoteFileRetriever remoteFileRetriever;
    private WebServer webServer;
    
    /**
     * @throws IOException not in this test
     */
    @Test(timeout = 8000)
    public void getChangelogOK() throws IOException {
        final PluginManager pluginManager = EasyMock.createMock(PluginManager.class);
        EasyMock.expect(pluginManager.getUpdateSiteBaseURL()).andReturn(BASE_URL);
        EasyMock.replay(pluginManager);
        
        webServer = new WebServer(BASE_URL);
        webServer.serveFileContents(CHANGELOG_TXT, CHANGELOG_CONTENTS);

        try {
            remoteFileRetriever = new DefaultRemoteFileRetriever(pluginManager);
            Assert.assertEquals(CHANGELOG_CONTENTS, remoteFileRetriever.getFileContents(CHANGELOG_TXT));
        } finally {
            webServer.stop();
            EasyMock.verify(pluginManager);
        }
    }
}

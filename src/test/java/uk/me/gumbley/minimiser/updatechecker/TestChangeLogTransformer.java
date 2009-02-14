package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Tests the transformation of change log in Sections to HTML
 * fragents.
 * 
 * @author matt
 *
 */
public final class TestChangeLogTransformer extends LoggingTestCase {
    private DefaultChangeLogTransformer changeLogTransformer;
    private File testLog;

    /**
     * @throws ParseException 
     * @throws IOException 
     * 
     */
    @Before
    public void getUsualPrerequisites() throws IOException, ParseException {
        testLog = new File("src/test/resources/uk/me/gumbley/minimiser/updatechecker/testchanges.txt");
        Assert.assertTrue(testLog.exists());
        changeLogTransformer = new DefaultChangeLogTransformer();
    }
    
    /**
     * @throws IOException
     * @throws ParseException 
     */
    @Test
    public void getNoSections() throws IOException, ParseException {
        final String subsection = changeLogTransformer.readFileSubsection(
            new ComparableVersion("v78"),
            new ComparableVersion("v90"), testLog);
        Assert.assertTrue(subsection.matches("<html><body></body></html>"));
        
    }
}

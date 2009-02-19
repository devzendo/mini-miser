package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.updatechecker.ChangeLogSectionParser.Section;


/**
 * Tests that the current change log won't make the change log
 * section parser blow up
 * @author matt
 *
 */
public final class TestCurrentChangeLogParses extends LoggingTestCase {
    private ChangeLogSectionParser sectionParser;

    /**
     * @throws ParseException never, unless the change log is bad
     * @throws IOException never
     * 
     */
    @Test
    public void currentChangeLogParses() throws IOException, ParseException {
        final InputStream resourceAsStream = Thread.currentThread().
            getContextClassLoader().
            getResourceAsStream("changelog.txt");
        
        sectionParser = new ChangeLogSectionParser(resourceAsStream);
        
        final List<Section> allVersionSections = sectionParser.getAllVersionSections();
        Assert.assertTrue(allVersionSections.size() > 0);
    }
}

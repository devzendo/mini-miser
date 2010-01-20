package org.devzendo.minimiser.updatechecker;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.logging.LoggingTestCase;
import org.devzendo.minimiser.updatechecker.ChangeLogSectionParser.Section;
import org.junit.Assert;
import org.junit.Test;



/**
 * Tests that the current change log won't make the change log
 * section parser blow up
 * @author matt
 *
 */
public final class TestCurrentChangeLogParses extends LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestCurrentChangeLogParses.class);
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
            getResourceAsStream("framework-changelog.txt");
        
        sectionParser = new ChangeLogSectionParser(resourceAsStream);
        
        final List<Section> allVersionSections = sectionParser.getAllVersionSections();
        Assert.assertTrue(allVersionSections.size() > 0);
        LOGGER.debug("Parsed " + allVersionSections.size() + " sections");
    }
}

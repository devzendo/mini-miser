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

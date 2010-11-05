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
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.devzendo.minimiser.updatechecker.ChangeLogSectionParser.Section;
import org.devzendo.minimiser.version.ComparableVersion;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;



/**
 * Tests that the change log transformer correctly returns the relevant
 * sections of a log file, transformed to HTML.
 *
 * @author matt
 *
 */
public final class TestChangeLogSectionParser {
    private static final Logger LOGGER = Logger
            .getLogger(TestChangeLogSectionParser.class);
    private ChangeLogSectionParser sectionParser;

    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

    /**
     * @throws IOException
     *
     */
    private void getUsualPrerequisites() throws IOException {
        final File testLog = new File("src/test/resources/org/devzendo/minimiser/updatechecker/testchanges.txt");
        Assert.assertTrue(testLog.exists());
        sectionParser = new ChangeLogSectionParser(testLog);
    }

    /**
     * @throws IOException no
     * @throws ParseException no
     *
     */
    @Test
    public void versionsExistByLargeRange() throws IOException, ParseException {
        getUsualPrerequisites();

        final List<Section> sections = sectionParser.
            getVersionSections(
                new ComparableVersion("0.0.0"),
                new ComparableVersion("9.9.9")); // all sections
        assertAllVersionsOK(sections);
    }

    /**
     * @throws IOException no
     * @throws ParseException no
     *
     */
    @Test
    public void versionsExistByExplicitRequest() throws IOException, ParseException {
        getUsualPrerequisites();

        final List<Section> sections = sectionParser.
            getAllVersionSections();
        assertAllVersionsOK(sections);
    }

    private void assertAllVersionsOK(final List<Section> sections) {
        Assert.assertEquals(10, sections.size());

        checkSection100(sections.get(0));

        checkSection098(sections.get(1));

        checkSection097(sections.get(2));

        checkSection09(sections.get(3));

        checkSection08(sections.get(4));

        checkSection07(sections.get(5));

        checkSection06(sections.get(6));

        checkSection05(sections.get(7));

        checkSection05alpha(sections.get(8));

        checkSection03(sections.get(9));
    }

    private void checkSection03(final Section section) {
        Assert.assertEquals(new ComparableVersion("0.3"), section.getVersion());
        Assert.assertEquals("30/12/2008", section.getDateText());
        Assert.assertEquals("no chronological sorting", section.getTitleText());
        Assert.assertEquals("", section.getInformationText());
    }

    private void checkSection05alpha(final Section section) {
        Assert.assertEquals(new ComparableVersion("0.5-alpha"), section.getVersion());
        Assert.assertEquals("25/12/2009", section.getDateText());
        Assert.assertEquals("separators are removed", section.getTitleText());
        Assert.assertEquals("", section.getInformationText());
    }

    private void checkSection05(final Section section) {
        Assert.assertEquals(new ComparableVersion("0.5"), section.getVersion());
        Assert.assertEquals("01/01/1970", section.getDateText());
        Assert.assertEquals("dawn of a new epoch", section.getTitleText());
        Assert.assertEquals("", section.getInformationText());
    }

    private void checkSection06(final Section section) {
        Assert.assertEquals(new ComparableVersion("0.6"), section.getVersion());
        Assert.assertEquals("22/02/1969", section.getDateText());
        Assert.assertEquals("way back, man", section.getTitleText());
        Assert.assertEquals("", section.getInformationText());
    }

    private void checkSection07(final Section section) {
        Assert.assertEquals(new ComparableVersion("0.7"), section.getVersion());
        Assert.assertEquals("", section.getDateText());
        Assert.assertEquals("more bug fixes", section.getTitleText());
        Assert.assertEquals("* singleton list\n", section.getInformationText());
    }

    private void checkSection08(final Section section) {
        Assert.assertEquals(new ComparableVersion("0.8"), section.getVersion());
        Assert.assertEquals("", section.getDateText());
        Assert.assertEquals("bug fixes", section.getTitleText());
        Assert.assertEquals("fixed several bugs\n", section.getInformationText());
    }

    private void checkSection09(final Section section) {
        Assert.assertEquals(new ComparableVersion("0.9"), section.getVersion());
        Assert.assertEquals("", section.getDateText());
        Assert.assertEquals("", section.getTitleText());
        Assert.assertEquals("thought I'd release again\n* a list of one is valid\n", section.getInformationText());
    }

    private void checkSection097(final Section section) {
        Assert.assertEquals(new ComparableVersion("0.9.7"), section.getVersion());
        Assert.assertEquals("", section.getDateText());
        Assert.assertEquals("colon separating witty title not out of order", section.getTitleText());
        Assert.assertEquals("* now handles left handed flange benders"
            + " that are so long that they\n  stretch onto a separate"
            + " line but are part of the same bullet and\n  so don't"
            + " get line breaks interspersed.\n* and bullet points\n", section.getInformationText());
    }

    private void checkSection098(final Section section) {
        Assert.assertEquals(new ComparableVersion("0.9.8"), section.getVersion());
        Assert.assertEquals("", section.getDateText());
        Assert.assertEquals("another witty title", section.getTitleText());
        Assert.assertEquals("Fixed some bugs\n* with a\n* simple list\nand some text\n", section.getInformationText());
    }

    private void checkSection100(final Section section) {
        Assert.assertEquals(new ComparableVersion("1.0.0"), section.getVersion());
        Assert.assertEquals("", section.getDateText());
        Assert.assertEquals("witty title goes here", section.getTitleText());
        Assert.assertEquals("This is the first major release.\nBlah\nBlah\nBlah\n", section.getInformationText());
    }

    /**
     * @throws IOException no
     * @throws ParseException yes
     */
    @Test(expected = ParseException.class)
    public void testBadVersionsThrow() throws IOException, ParseException {
        try {
            final File testLog = new File("src/test/resources/org/devzendo/minimiser/updatechecker/badversionchanges.txt");
            Assert.assertTrue(testLog.exists());
            sectionParser = new ChangeLogSectionParser(testLog);
            sectionParser.getVersionSections(
                new ComparableVersion("0.0.0"),
                new ComparableVersion("9.9.9")); // all sections
        } catch (final ParseException pe) {
            LOGGER.debug("Bad version: " + pe.getMessage());
            throw pe;
        }
    }

    /**
     * @throws IOException no
     * @throws ParseException no
     */
    @Test
    public void testVersionOrderingWithClassifiers() throws IOException, ParseException {
        getUsualPrerequisites();
        final List<Section> sections = sectionParser.getVersionSections(
            new ComparableVersion("v0.5-alpha"),
            new ComparableVersion("v0.5"));
        Assert.assertEquals(2, sections.size());
    }

}

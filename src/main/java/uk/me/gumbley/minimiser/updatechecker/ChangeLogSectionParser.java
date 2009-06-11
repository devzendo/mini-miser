package uk.me.gumbley.minimiser.updatechecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Parses a change log into Sections, and allows for a range of
 * them to be obtainned.
 * 
 * @author matt
 *
 */
public final class ChangeLogSectionParser {
    private static final Logger LOGGER = Logger
            .getLogger(ChangeLogSectionParser.class);

    private static final String TITLE_REGEX = "(.*?)";

    private static final String DATE_REGEX = "(\\d{1,2}/\\d{1,2}/\\d{2,4})";

    private static final String SEPARATOR_REGEX = "\\s*[-:]?\\s*";

    private final Matcher versionDateTitleMatcher;
    private final InputStream logInputStream;
    
    // state used when building up a new Section
    private String versionText;
    private ArrayList<String> informationTextLines;
    private String dateText;
    private String titleText;
    
    /**
     * A Section contains all information about a particular
     * version of the software described in the change log.
     * 
     * @author matt
     *
     */
    public final class Section implements Comparable<Section> {
        private final String versionText;
        private final String informationText;
        private final String dateText;
        private final String titleText;
        private final ComparableVersion comparableVersion;

        /**
         * Create a Section
         * @param version the version number of this section
         * @param date the date it was released on
         * @param title the short title for this release
         * @param information all information pertaining to the
         * release
         */
        public Section(final String version, final String date, final String title, final String information) {
            this.versionText = version;
            this.dateText = date;
            this.titleText = title;
            this.informationText = information;
            this.comparableVersion = new ComparableVersion(version);
        }

        /**
         * @return the full information pertaining to this
         * release
         */
        public String getInformationText() {
            return informationText;
        }

        /**
         * @return the version of this release
         */
        public String getVersionText() {
            return versionText;
        }

        /**
         * @return the version of this release, as a ComparableVersion
         */
        public ComparableVersion getVersion() {
            return comparableVersion;
        }

        /**
         * @return the date of this release
         */
        public String getDateText() {
            return dateText;
        }

        /**
         * @return the short title of this release
         */
        public String getTitleText() {
            return titleText;
        }

        /**
         * Sort in reverse ComparableVersion order - highest first
         * {@inheritDoc}
         */
        public int compareTo(final Section o) {
            return comparableVersion.compareTo(o.comparableVersion) * -1;
        }
    }
    
    /**
     * A Handler for Sections as they are processed.
     * @author matt
     *
     */
    private interface SectionHandler {
        /**
         * Handle the section in some way
         * @param section
         */
        void handleSection(Section section);
    }
    
    /**
     * Create a ChangeLogSectionParser
     * @param testLog the change log file to parse
     * @throws IOException on file problems
     */
    public ChangeLogSectionParser(final File testLog) throws IOException {
        this(new FileInputStream(testLog));
    }

    /**
     * Create a ChangeLogSectionParser
     * @param inputStream the change log input stream to parse
     */
    public ChangeLogSectionParser(final InputStream inputStream) {
        this.logInputStream = inputStream;
        versionDateTitleMatcher = Pattern.compile("^" + ComparableVersion.VERSION_REGEX
            + SEPARATOR_REGEX + DATE_REGEX + "?" + SEPARATOR_REGEX + TITLE_REGEX + SEPARATOR_REGEX + "$").matcher("");
    }
    
    /**
     * Obtain a list of Sections encompassing two versions, sorted
     * by version
     * @param fromVersion the earliest version to return
     * @param toVersion the latest version to return
     * @return the List, which may be empty
     * @throws IOException on read failure
     * @throws ParseException on version number validation failure
     */
    public List<Section> getVersionSections(final ComparableVersion fromVersion,
        final ComparableVersion toVersion) throws IOException, ParseException {
        final ArrayList<Section> outputSections = new ArrayList<Section>();
        processChangeLogfile(new SectionHandler() {
            public void handleSection(final Section section) {
                if (sectionIsInsideRange(section, fromVersion, toVersion)) {
                    outputSections.add(section);
                }
            }
        });
        Collections.sort(outputSections);
        return outputSections;
    }

    /**
     * Obtain a list of all Sections, sorted by version
     * @return the List, which may be empty
     * @throws IOException on read failure
     * @throws ParseException on version number validation failure
     */
    public List<Section> getAllVersionSections() throws IOException, ParseException {
        final ArrayList<Section> outputSections = new ArrayList<Section>();
        processChangeLogfile(new SectionHandler() {
            public void handleSection(final Section section) {
                outputSections.add(section);
            }
        });
        Collections.sort(outputSections);
        return outputSections;
    }

    private void processChangeLogfile(final SectionHandler rangeChecker)
            throws IOException, ParseException {
        resetSectionState();
        int lineNo = 0;
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(logInputStream));
        try {
            while (true) {
                lineNo++;
                final String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                processLine(line, lineNo, rangeChecker);
            }
            endOfFileReached(rangeChecker);
        } catch (final IllegalArgumentException iae) {
            // from the ComparableVersion ctor
            final String warning = "Parse failure on line " + lineNo + ": " + iae.getMessage();
            LOGGER.warn(warning);
            throw new ParseException(warning, iae);
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }

    private void resetSectionState() {
        versionText = "";
        informationTextLines = new ArrayList<String>();
        dateText = "";
        titleText = "";
    }

    private void endOfFileReached(final SectionHandler sectionHandler) {
        LOGGER.debug("End of file found; checking for emission of previous section");
        emitPreviousSection(sectionHandler);
        return;
    }

    private void processLine(final String line, final int lineNo, final SectionHandler sectionHandler) throws IOException {
        LOGGER.debug("Processing line " + lineNo + "'" + line + "'");
        if (line.matches("^#.*$")) {
            return;
        }
        if (processHeaderLine(line, lineNo, sectionHandler)) {
        } else {
            informationTextLines.add(line);
        }
    }

    private boolean processHeaderLine(final String line, final int lineNo, final SectionHandler sectionHandler) throws IOException {
        if (line.matches("^\\s*$") || line.matches("^[-=]+$")) { // ignore blank lines and --- === rulers
            return true;
        }
        LOGGER.debug("Testing for header line");
        versionDateTitleMatcher.reset(line);
        if (versionDateTitleMatcher.lookingAt()) {
            LOGGER.debug("Header found; checking for emission of previous section");
            emitPreviousSection(sectionHandler);
            versionText = versionDateTitleMatcher.group(1)
                        + (versionDateTitleMatcher.group(2) == null ? "" : versionDateTitleMatcher.group(2))
                        + (versionDateTitleMatcher.group(3) == null ? "" : versionDateTitleMatcher.group(3));
            dateText = versionDateTitleMatcher.group(4) == null ? "" : versionDateTitleMatcher.group(4);
            titleText = versionDateTitleMatcher.group(5) == null ? "" : versionDateTitleMatcher.group(5);
            LOGGER.debug("versionText='" + versionText + "'");
            LOGGER.debug("dateText='" + dateText + "'");
            LOGGER.debug("titleText='" + titleText + "'");
            return true;
        }
        return false;
    }

    private void emitPreviousSection(final SectionHandler sectionHandler) {
        if (!versionText.equals("")) {
            final Section previous = new Section(versionText, dateText, titleText,
                mergeInformationLines());
            LOGGER.debug("passing section to handler");
            sectionHandler.handleSection(previous);
            resetSectionState();
        }
    }

    private String mergeInformationLines() {
        // go from the first non-empty to the last non-empty line, append \n
        // to the end of each, and return.
        final StringBuilder sb = new StringBuilder();
        int first = 0;
        int last = informationTextLines.size() - 1;
        for (int i = 0; i < informationTextLines.size(); i++) {
            if (informationTextLines.get(i).length() != 0) {
                first = i;
                break;
            }
        }
        for (int i = informationTextLines.size() - 1; i >= 0; i--) {
            if (informationTextLines.get(i).length() != 0) {
                last = i;
                break;
            }
        }
        for (int i = first; i <= last; i++) {
            sb.append(informationTextLines.get(i));
            sb.append("\n");
        }
        return sb.toString();
    }

    private boolean sectionIsInsideRange(final Section section, final ComparableVersion fromVersion, final ComparableVersion toVersion) {
        if (section != null) {
            return ComparableVersion.inRange(section.getVersion(), fromVersion, toVersion);
        }
        return false;
    }
}

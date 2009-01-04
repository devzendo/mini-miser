package uk.me.gumbley.minimiser.updatechecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.string.StringUtils;

public final class ChangeLogSectionParser {
    private static final Logger LOGGER = Logger
            .getLogger(ChangeLogSectionParser.class);

    private static final String TITLE_REGEX = "(.*?)";

    private static final String DATE_REGEX = "(\\d{1,2}/\\d{1,2}/\\d{2,4})";

    private static final String SEPARATOR_REGEX = "\\s*[-:]?\\s*";

    private final File log;
    
    // state used when building up a new Section
    private boolean buildingInformationSection;
    private String versionText;
    private ArrayList<String> informationTextLines;
    private String dateText;
    private String titleText;

    private Matcher versionDateTitleMatcher;
    
    
    public class Section implements Comparable<Section> {
        private final String versionText;
        private final String informationText;
        private final String dateText;
        private final String titleText;
        private ComparableVersion comparableVersion;

        public Section(final String version, final String date, final String title, final String information) {
            this.versionText = version;
            this.dateText = date;
            this.titleText = title;
            this.informationText = information;
            this.comparableVersion = new ComparableVersion(version);
        }

        public final String getInformationText() {
            return informationText;
        }

        public final String getVersionText() {
            return versionText;
        }

        public ComparableVersion getVersion() {
            return comparableVersion;
        }

        public final String getDateText() {
            return dateText;
        }

        public final String getTitleText() {
            return titleText;
        }

        /**
         * Sort in reverse ComparableVersoin order - highest first
         * {@inheritDoc}
         */
        public int compareTo(final Section o) {
            return comparableVersion.compareTo(o.comparableVersion) * -1;
        }
    }
    
    private interface SectionHandler {
        public void handleSection(Section section);
    }
    
    public ChangeLogSectionParser(final File testLog) {
        this.log = testLog;
        versionDateTitleMatcher = Pattern.compile("^" + ComparableVersion.VERSION_REGEX
            + SEPARATOR_REGEX + DATE_REGEX + "?" + SEPARATOR_REGEX + TITLE_REGEX + SEPARATOR_REGEX + "$").matcher("");
    }

    public List<Section> getVersionSections(final ComparableVersion fromVersion,
        final ComparableVersion toVersion) throws IOException, ParseException {
        buildingInformationSection = false;
        resetSectionState();
        int lineNo = 0;
        final ArrayList<Section> outputSections = new ArrayList<Section>();
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(log));
        final SectionHandler rangeChecker = new SectionHandler() {
            public void handleSection(final Section section) {
                if (sectionIsInsideRange(section, fromVersion, toVersion)) {
                    outputSections.add(section);
                }
            }
        }; 
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
        Collections.sort(outputSections);
        return outputSections;
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
        if (line.matches("^\\s*$") || line.matches("^[-=]+$")) {
            return true;
        }
        LOGGER.debug("Testing for header line");
        versionDateTitleMatcher.reset(line);
        if (versionDateTitleMatcher.lookingAt()) {
            LOGGER.debug("Header found; checking for emission of previous section");
            emitPreviousSection(sectionHandler);
            versionText = versionDateTitleMatcher.group(1) +
                          (versionDateTitleMatcher.group(2) == null ? "" : versionDateTitleMatcher.group(2)) +
                          (versionDateTitleMatcher.group(3) == null ? "" : versionDateTitleMatcher.group(3));
            dateText = versionDateTitleMatcher.group(4) == null ? "" : versionDateTitleMatcher.group(4);
            titleText = versionDateTitleMatcher.group(5) == null ? "" : versionDateTitleMatcher.group(5);
            buildingInformationSection = true;
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

package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.updatechecker.ChangeLogSectionParser.Section;

/**
 * Transforms change log details into HTML for display.
 * 
 * @author matt
 *
 */
public final class DefaultChangeLogTransformer implements ChangeLogTransformer {
    private static final String SEPARATOR = " - ";

    private static final Logger LOGGER = Logger.getLogger(DefaultChangeLogTransformer.class);
    
    private final Matcher bulletMatcher = Pattern.compile("^\\s*\\*\\s*(.*)$").matcher("");
    

    /**
     * {@inheritDoc}
     * @throws ParseException 
     */
    public String readFileSubsection(final ComparableVersion currentVersion, 
            final ComparableVersion latestVersion, final File changeLogFile) throws IOException, ParseException {
        final ChangeLogSectionParser parser = new ChangeLogSectionParser(changeLogFile);
        final List<Section> versionSections = parser.getVersionSections(currentVersion, latestVersion);
        LOGGER.debug("Transforming " + versionSections.size() + " sections ["
            + currentVersion.toString() + ", " + latestVersion.toString() + "] to HTML");
        final StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        for (int i = 0; i < versionSections.size(); i++) {
            final Section section  = versionSections.get(i);
            sb.append(addSection(section));
            if (i != versionSections.size() - 1) {
                sb.append("</p>");
            }
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    private StringBuilder addSection(final Section section) {
        final StringBuilder sb = new StringBuilder();
        
        // version text
        sb.append(surround("b", section.getVersion().toString()));
        
        // date (optional)
        if (section.getDateText().length() != 0) {
            sb.append(SEPARATOR);
            sb.append(section.getDateText());
        }
        
        // title (optional)
        if (section.getTitleText().length() != 0) {
            sb.append(SEPARATOR);
            sb.append(surround("em", section.getTitleText()));
        }
        
        sb.append("</br>");
        
        sb.append(transformedInformationText(section.getInformationText()));
        
        return sb;
    }

    private String transformedInformationText(final String informationText) {
        final StringBuilder sb = new StringBuilder();
        boolean inList = false;
        final String[] informationLines = informationText.split("[\\r\\n]+");
        for (int i = 0; i < informationLines.length; i++) {
            String line = informationLines[i];
            LOGGER.debug("Input line '" + line + "'");
            
            bulletMatcher.reset(line);
            if (bulletMatcher.lookingAt()) {
                line = bulletMatcher.group(1);
                if (!inList) {
                    inList = true;
                    sb.append("<ul>");
                }
            } else {
                if (inList) {
                    inList = false;
                    LOGGER.debug("no bullet match - end of ul");
                    sb.append("</ul>");
                }
            }
            if (inList) {
                LOGGER.debug("in list");

                sb.append("<li>");
            }
            sb.append(line);
            if (inList) {
                sb.append("</li>");
                // note check when adding <br> - no line break necessary
            }
            if (i == informationLines.length - 1) {
                LOGGER.debug("last line");
                // was last line the last line of a bullet?
                if (inList) {
                    inList = false;
                    LOGGER.debug("end of ul");

                    sb.append("</ul>");
                }
            } else {
                if (!inList) {
                    sb.append("</br>");
                }
            }
        }
        return sb.toString();
    }

    private String surround(final String tag, final String text) {
        return "<" + tag + ">" + text + "</" + tag + ">";
    }
}

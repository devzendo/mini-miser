package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;
import java.util.List;

import uk.me.gumbley.minimiser.updatechecker.ChangeLogSectionParser.Section;

/**
 * Transforms change log details into HTML for display.
 * 
 * @author matt
 *
 */
public final class DefaultChangeLogTransformer implements ChangeLogTransformer {

    /**
     * {@inheritDoc}
     * @throws ParseException 
     */
    public String readFileSubsection(final ComparableVersion currentVersion, 
            final ComparableVersion latestVersion, final File changeLogFile) throws IOException, ParseException {
        final ChangeLogSectionParser parser = new ChangeLogSectionParser(changeLogFile);
        final List<Section> versionSections = parser.getVersionSections(currentVersion, latestVersion);
        final StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        
        sb.append("</body></html>");
        return sb.toString();
    }
}

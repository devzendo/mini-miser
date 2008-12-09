package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;

/**
 * Transforms a change log file by taking the relevant sections - any changes
 * between latest version and the version you're running, and translates them
 * into an appropriate display form, e.g. HTML.
 * 
 * @author matt
 *
 */
public interface ChangeLogTransformer {

    /**
     * Scan through the change log file, finding the subsection that describes
     * changes between the current (runtime) version and the latest version
     * available from the update site. Transform the subsection into the
     * appropriate display form, e.g. HTML.
     * @param currentVersion the current runtime version
     * @param latestVersion the latest version available on the update site
     * @param changeLogFile the change log file as downloaded from the update
     * site
     * @return the transformed subsection
     * @throws IOException on file read or transformation failure
     */
    String readFileSubsection(String currentVersion, String latestVersion, File changeLogFile) throws IOException;
}
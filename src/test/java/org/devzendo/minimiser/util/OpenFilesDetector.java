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

package org.devzendo.minimiser.util;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.executor.IteratorExecutor;
import org.devzendo.commoncode.os.OSTypeDetect;
import org.devzendo.commoncode.os.OSTypeDetect.OSType;


/**
 * Utility code to detect open files.
 * @author matt
 *
 */
public final class OpenFilesDetector {
    private static final Logger LOGGER = Logger
            .getLogger(OpenFilesDetector.class);
    private OpenFilesDetector() {
        // no instantiations
    }
    
    /**
     * Only for Linux, are there any files open in the given directory?
     * @param directory a directory
     * @param filePrefix the common prefix of any files to narrow the search of open files 
     * @return true if there are any open files.
     * @throws IllegalStateException if called on platforms other than Linux.
     */
    public static boolean anyOpenFiles(final String directory, final String filePrefix) throws IllegalStateException {
        if (filePrefix.contains(File.separator)) {
            throw new IllegalArgumentException("filePrefix parameter to anyOpenFiles should be a db name, not a path");
        }
        if (OSTypeDetect.getInstance().getOSType() != OSType.Linux) {
            throw new IllegalStateException("anyOpenFiles called on nonsupported platform");
        }
        final ArrayList <String>cmd = new ArrayList<String>();
        cmd.add("lsof");
        cmd.add("-Fn");
        cmd.add("+D");
        cmd.add(directory);
        final IteratorExecutor ie = new IteratorExecutor((String[]) cmd.toArray(new String[0]));
        final String ourFilePath = String.format("n%s/%s", directory, filePrefix);
        LOGGER.debug("Checking for lines starting with '" + ourFilePath + "'");
        boolean anyOpen = false;
        while (ie.hasNext()) {
            final String line = ie.next().toString();
            if (line.startsWith(ourFilePath)) {
                anyOpen = true;
                LOGGER.debug(String.format("Open file: '%s'", line));
            } else {
                LOGGER.debug("Line does not start with correct text '" + line + "'");
            }
        }
        final int exitValue = ie.getExitValue();
        final String errMsg = String.format("lsof of %s returned %d : %s", directory, exitValue, anyOpen ? "FILES ARE OPEN" : "NO FILES OPEN");
        LOGGER.debug(errMsg);
        return anyOpen;
    }

}

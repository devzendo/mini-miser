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

package org.devzendo.minimiser.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.os.OSTypeDetect;
import org.devzendo.commoncode.os.OSTypeDetect.OSType;
import org.devzendo.minimiser.util.OpenFilesDetector;
import org.junit.Assert;
import org.junit.Test;



/**
 * Test the OpenFilesDetector. This isn't in the util
 * package, since it uses getDatabaseDirectory, which is
 * part of PersistenceTestCase.
 * 
 * @author matt
 *
 */
public final class TestOpenFilesInDbDirDetector extends DummyAppPluginManagerPersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestOpenFilesInDbDirDetector.class);
    /**
     * 
     */
    @Test
    public void testNothingOpen() {
        if (OSTypeDetect.getInstance().getOSType() != OSType.Linux) {
            return;
        }
        Assert.assertFalse(
            OpenFilesDetector.anyOpenFiles(getDatabaseDirectory().getAbsolutePath(), "gobbledegook"));
    }
    
    /**
     * @throws IOException on failure
     */
    @Test
    public void testOpenFileDetection() throws IOException {
        if (OSTypeDetect.getInstance().getOSType() != OSType.Linux) {
            return;
        }
        final File file = new File(getDatabaseDirectory(), "gobbledegook");
        file.deleteOnExit();
        try {
            LOGGER.info("Creating " + file);
            final PrintWriter pw = new PrintWriter(new FileOutputStream(file));
            pw.println("test file");
            LOGGER.info("is it open?");
            final boolean shouldBeOpen = OpenFilesDetector.anyOpenFiles(getDatabaseDirectory().getAbsolutePath(), "gobbledegook");
            LOGGER.info("closing");
            pw.close();
            LOGGER.info("is it not open?");
            final boolean shouldBeClosed = OpenFilesDetector.anyOpenFiles(getDatabaseDirectory().getAbsolutePath(), "gobbledegook");
            Assert.assertTrue("Should have detected an open file", shouldBeOpen);
            Assert.assertFalse("Should have detected that a previously open file was no longer open after a close", shouldBeClosed);
        } finally {
            LOGGER.info("deleting");
            file.delete();
        }
    }
}

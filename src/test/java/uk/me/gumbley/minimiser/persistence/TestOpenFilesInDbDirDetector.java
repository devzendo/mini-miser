package uk.me.gumbley.minimiser.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.commoncode.os.OSTypeDetect;
import uk.me.gumbley.commoncode.os.OSTypeDetect.OSType;
import uk.me.gumbley.minimiser.util.OpenFilesDetector;


/**
 * Test the OpenFilesDetector. This isn't in the util
 * package, since it uses getDatabaseDirectory, which is
 * part of PersistenceTestCase.
 * 
 * @author matt
 *
 */
public final class TestOpenFilesInDbDirDetector extends PersistenceUnittestCase {
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
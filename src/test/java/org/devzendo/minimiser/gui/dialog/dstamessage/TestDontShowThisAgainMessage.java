package org.devzendo.minimiser.gui.dialog.dstamessage;

import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;

import junit.framework.Assert;

import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.PrefsFactory;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests that a message can be flagged in prefs such that it is not shown again.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/gui/dialog/dstamessage/DSTATestCase.xml")
public final class TestDontShowThisAgainMessage extends SpringLoaderUnittestCase {

    private Prefs prefs;
    private File prefsFile;
    private DSTAMessageFactory messageFactory;
    private final DSTAMessageId messageId = DSTAMessageId.TEST;


    /**
     * Get all necessaries
     * @throws IOException on prefs file creation failure
     */
    @Before
    public void getMediatorPrerequisites() throws IOException {
        prefs = TestPrefs.createUnitTestPrefsFile();
        prefsFile = new File(prefs.getAbsolutePath());
        prefsFile.deleteOnExit();
        final PrefsFactory prefsFactory = getSpringLoader().getBean("&prefs", PrefsFactory.class);
        prefsFactory.setPrefs(prefs);

        messageFactory = getSpringLoader().getBean("dstaMessageFactory", DSTAMessageFactory.class);
    }

    /**
     *
     */
    @Test
    public void userCanSayGetOuttaMyFace() {
        Assert.assertFalse(prefs.isDontShowThisAgainFlagSet(messageId.toString()));

        final DSTAMessage message = messageFactory.possiblyShowMessage(messageId, "an annoying message");
        Assert.assertNotNull(message);

        final StubDSTAMessage stubMessage = (StubDSTAMessage) message;
        stubMessage.getOutOfMyFace(); // "click"....

        Assert.assertTrue(prefs.isDontShowThisAgainFlagSet(messageId.toString()));

        Assert.assertNull(messageFactory.possiblyShowMessage(messageId, "an annoying message"));
    }

    /**
     *
     */
    @Test
    public void userCanRejectAComplexMessage() {
        final StubDSTAMessage stubMessage = (StubDSTAMessage) messageFactory.possiblyShowMessage(messageId, new JLabel("an annoying message"));
        stubMessage.getOutOfMyFace(); // "click"....

        // note that the message key is the same...
        Assert.assertNull(messageFactory.possiblyShowMessage(messageId, "an annoying message"));

    }
}

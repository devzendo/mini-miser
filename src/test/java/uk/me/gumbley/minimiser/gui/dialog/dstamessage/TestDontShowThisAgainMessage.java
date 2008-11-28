package uk.me.gumbley.minimiser.gui.dialog.dstamessage;

import java.io.File;
import java.io.IOException;
import javax.swing.JLabel;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.PrefsFactory;
import uk.me.gumbley.minimiser.prefs.TestPrefs;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;


/**
 * Tests that a message can be flagged in prefs such that it is not shown again.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/gui/dialog/dstamessage/DSTATestCase.xml")
public final class TestDontShowThisAgainMessage extends SpringLoaderUnittestCase {

    private Prefs prefs;
    private File prefsFile;
    private DSTAMessageFactory messageFactory;
    private DSTAMessageId messageId = DSTAMessageId.TEST;
    

    /**
     * Get all necessaries
     * @throws IOException on prefs file creation failure
     */
    @SuppressWarnings("unchecked")
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

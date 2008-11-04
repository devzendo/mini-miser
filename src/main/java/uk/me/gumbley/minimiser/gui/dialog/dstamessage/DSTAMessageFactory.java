package uk.me.gumbley.minimiser.gui.dialog.dstamessage;

import java.awt.Component;

/**
 * Creates popup messages that allow the user to indicate that they don't want
 * to see the message again, with this choice being stored in prefs.
 * 
 * @author matt
 *
 */
public interface DSTAMessageFactory {

    /**
     * Possibly show a message, with the option of disabling repeated showings.
     * @param messageId the key for this message
     * @param string the text of the message
     * @return a DSTAMessage
     */
    DSTAMessage possiblyShowMessage(DSTAMessageId messageId, String string);

    /**
     * Possibly show a message, with the option of disabling repeated showings.
     * @param messageId the key for this message
     * @param content a graphical component holding the message
     * @return a DSTAMessage
     */
    DSTAMessage possiblyShowMessage(DSTAMessageId messageId, Component content);

    /**
     * Indicate that this message should not be shown again; called by messages
     * when the user checks the DSTA checkbox
     * @param messageId the key for this message
     */
    void setDontShowThisAgain(DSTAMessageId messageId);
}

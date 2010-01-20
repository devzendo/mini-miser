package org.devzendo.minimiser.gui.dialog.dstamessage;

import java.awt.Component;

/**
 * Stub DSTA Message that allows the DSTA 'checkbox' to be checked by tests.
 * @author matt
 *
 */
public final class StubDSTAMessage extends AbstractDSTAMessage {

    /**
     * Construct a stub message
     * @param factory the factory
     * @param msgId the key for this message
     * @param string the message text
     */
    public StubDSTAMessage(final DSTAMessageFactory factory, final DSTAMessageId msgId, final String string) {
        super(factory, msgId, string);
    }


    /**
     * Construct a stub message
     * @param factory the factory
     * @param msgId the key for this message
     * @param content the message content
     */
    public StubDSTAMessage(final StubDSTAMessageFactory factory, final DSTAMessageId msgId, final Component content) {
        super(factory, msgId, content);
    }

    /**
     * the stub wants this message blocking
     */
    public void getOutOfMyFace() {
        setDontShowThisAgain();
    }
}

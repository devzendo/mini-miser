package org.devzendo.minimiser.gui.dialog.dstamessage;

import java.awt.Component;

import org.devzendo.minimiser.prefs.Prefs;

/**
 * Provides the basic prefs interaction for DSTAMessageFactories.
 * @author matt
 *
 */
public abstract class AbstractDSTAMessageFactory implements DSTAMessageFactory {
    private final Prefs prefs;

    /**
     * Construct the AbstractDSTAMessageFactory given the prefs used to store
     * the DSTA flags.
     * @param preferences the prefs in which the DSTA flags are stored.
     */
    public AbstractDSTAMessageFactory(final Prefs preferences) {
        this.prefs = preferences;
    }

    /**
     * {@inheritDoc}
     */
    public final DSTAMessage possiblyShowMessage(final DSTAMessageId messageId, final String string) {
        if (!prefs.isDontShowThisAgainFlagSet(messageId.toString())) {
            return reallyShowMessage(messageId, string);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final DSTAMessage possiblyShowMessage(final DSTAMessageId messageId, final Component content) {
        if (!prefs.isDontShowThisAgainFlagSet(messageId.toString())) {
            return reallyShowMessage(messageId, content);
        }
        return null;
    }
    
    /**
     * The message has not been blocked, so present a dialog showing it.
     * @param messageId the key for this message
     * @param string the message text.
     * @return the DSTAMessage (not that anything can really be done with it)
     */
    protected abstract DSTAMessage reallyShowMessage(final DSTAMessageId messageId, final String string);


    /**
     * The message has not been blocked, so present a dialog showing it.
     * @param messageId the key for this message
     * @param content the message content
     * @return the DSTAMessage (not that anything can really be done with it)
     */
    protected abstract DSTAMessage reallyShowMessage(final DSTAMessageId messageId, final Component content);

    /**
     * {@inheritDoc}
     */
    public final void setDontShowThisAgain(final DSTAMessageId messageId) {
        prefs.setDontShowThisAgainFlag(messageId.toString());
    }
}

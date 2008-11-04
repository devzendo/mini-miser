package uk.me.gumbley.minimiser.gui.dialog.dstamessage;

import java.awt.Component;
import java.awt.Frame;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * A DSTAMessageFactory that creates real DSTA messages (i.e. backed by GUI
 * components)
 * 
 * @author matt
 *
 */
public final class DefaultDSTAMessageFactoryImpl extends AbstractDSTAMessageFactory {
    private final Frame frame;

    /**
     * Construct the message factory, given prefs in which to store the
     * "don't show this again" flags
     * @param preferences the prefs for storage
     * @param mainFrame the main application frame
     */
    public DefaultDSTAMessageFactoryImpl(final Prefs preferences, final Frame mainFrame) {
        super(preferences);
        this.frame = mainFrame;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DSTAMessage reallyShowMessage(
            final DSTAMessageId messageId,
            final String string) {
        return new DefaultDSTAMessageImpl(this, frame, messageId, string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DSTAMessage reallyShowMessage(
            final DSTAMessageId messageId,
            final Component content) {
        return new DefaultDSTAMessageImpl(this, frame, messageId, content);
    }
}

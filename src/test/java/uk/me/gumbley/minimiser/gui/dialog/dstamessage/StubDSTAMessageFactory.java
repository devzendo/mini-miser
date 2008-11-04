package uk.me.gumbley.minimiser.gui.dialog.dstamessage;

import java.awt.Component;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * A DSTAMessageFactory that creates StubDSTAMessages, not real GUI ones.
 * @author matt
 *
 */
public final class StubDSTAMessageFactory extends AbstractDSTAMessageFactory {

    /**
     * Construct the stub DSTA Message factory
     * @param preferences the prefs used to store the DSTA flags
     */
    public StubDSTAMessageFactory(final Prefs preferences) {
        super(preferences);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DSTAMessage reallyShowMessage(final DSTAMessageId msgId, final String string) {
        return new StubDSTAMessage(this, msgId, string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DSTAMessage reallyShowMessage(final DSTAMessageId msgId, final Component content) {
        return new StubDSTAMessage(this, msgId, content);
    }

}

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

package org.devzendo.minimiser.gui.dialog.dstamessage;

import java.awt.Component;
import java.awt.Frame;

import javax.swing.SwingUtilities;

import org.devzendo.minimiser.prefs.MiniMiserPrefs;

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
    public DefaultDSTAMessageFactoryImpl(final MiniMiserPrefs preferences, final Frame mainFrame) {
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
        assert SwingUtilities.isEventDispatchThread();
        
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

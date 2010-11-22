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

package org.devzendo.minimiser.gui.messagequeueviewer;

import java.awt.Component;

import org.devzendo.commonapp.gui.panel.HTMLPanel;
import org.devzendo.minimiser.messagequeue.Message;

/**
 * A renderer for Simple Messages - just renders the text. Controls are left
 * up to subclasses.
 * 
 * @author matt
 *
 */
public abstract class AbstractSimpleMessageRenderer implements MessageRenderer {

    private final Message message;

    /**
     * Construct the renderer
     * @param msg the message to render
     */
    public AbstractSimpleMessageRenderer(final Message msg) {
        this.message = msg;
    }

    /**
     * {@inheritDoc}
     */
    public final Component render() {
        return new HTMLPanel(message.getMessageContent().toString());
    }
}

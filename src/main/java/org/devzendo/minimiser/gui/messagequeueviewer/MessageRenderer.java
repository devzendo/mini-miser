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

/**
 * MessageRenderers create graphical components that display the content
 * provided by Messages. 
 * <p>
 * The appropriate type of MessageRenderer for the type
 * of Message is created by the MessageRendererFactory.
 * @author matt
 *
 */
public interface MessageRenderer {

    /**
     * Render the message into a graphical component
     * @return the rendered component
     */
    Component render();

    /**
     * Render any controls for this message into a graphical component
     * @return the rendered controls
     */
    Component renderControls();
}

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


/**
 * A headless MessageQueueViewer for tests.
 * @author matt
 *
 */
public final class HeadlessMessageQueueViewer extends AbstractMessageQueueViewer {

    /**
     * Construct the viewer given its fcatory.
     * @param factory this viewer's fcatory
     */
    public HeadlessMessageQueueViewer(final MessageQueueViewerFactory factory) {
        super(factory);
    }
    
    /**
     * For use by unit tests, indicate that "the user has closed the viewer
     * dialog". 
     */
    public void closeViewer() {
        getMessageQueueViewerFactory().messageViewerClosed();
    }
}

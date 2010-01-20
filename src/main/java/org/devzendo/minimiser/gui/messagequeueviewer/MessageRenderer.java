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

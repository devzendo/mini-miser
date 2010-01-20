package org.devzendo.minimiser.gui.messagequeueviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.devzendo.minimiser.messagequeue.SimpleDSTAMessage;

/**
 * A renderer for SimpleDSTAMessages.
 * 
 * @author matt
 *
 */
public final class SimpleDSTAMessageRenderer extends AbstractSimpleMessageRenderer {

    private final SimpleDSTAMessage simpleDSTAMessage;

    /**
     * Construct the renderer
     * @param message the message to render
     */
    public SimpleDSTAMessageRenderer(final SimpleDSTAMessage message) {
        super(message);
        this.simpleDSTAMessage = message;
    }

    /**
     * {@inheritDoc}
     */
    public Component renderControls() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JCheckBox checkbox = new JCheckBox("Don't show this again");
        checkbox.setSelected(simpleDSTAMessage.dontShowAgain());
        checkbox.addItemListener(new ItemListener() {
            public void itemStateChanged(final ItemEvent e) {
                simpleDSTAMessage.setDontShowAgain(checkbox.isSelected());
            }
        });
        panel.add(checkbox, BorderLayout.WEST);
        return panel;
    }
}

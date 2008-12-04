package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import uk.me.gumbley.minimiser.messagequeue.BooleanFlagSettingMessage;

/**
 * Renders boolean flag-setting messages with a checkbox for the flag.
 * 
 * @author matt
 *
 */
public final class BooleanFlagSettingMessageRenderer extends AbstractSimpleMessageRenderer {
    private BooleanFlagSettingMessage booleanFlagSettingMessage;
    /**
     * Construct the renderer
     * @param message the message to render
     */
    public BooleanFlagSettingMessageRenderer(final BooleanFlagSettingMessage message) {
        super(message);
        this.booleanFlagSettingMessage = message;
    }

    /**
     * {@inheritDoc}
     */
    public Component renderControls() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JCheckBox checkbox = new JCheckBox(booleanFlagSettingMessage.getChoiceExplanationText());
        checkbox.setSelected(booleanFlagSettingMessage.isBooleanFlagSet());
        checkbox.addItemListener(new ItemListener() {
            public void itemStateChanged(final ItemEvent e) {
                booleanFlagSettingMessage.setBooleanFlagValue(checkbox.isSelected());
            }
        });
        panel.add(checkbox, BorderLayout.WEST);
        return panel;
    }
}

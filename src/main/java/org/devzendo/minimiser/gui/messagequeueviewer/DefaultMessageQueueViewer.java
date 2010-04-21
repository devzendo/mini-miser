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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.messagequeue.Message;
import org.devzendo.minimiser.messagequeue.MessageQueue;


/**
 * A Swing-based MessageQueueViewer.
 * 
 * @author matt
 *
 */
public final class DefaultMessageQueueViewer extends AbstractMessageQueueViewer {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultMessageQueueViewer.class);
    private final JDialog mDialog;
    private final MessageQueue mMessageQueue;
    private HTMLPanel mMessageSubjectPane;
    private HTMLPanel mNoMessagesTextPane;
    private final MessageRendererFactory mMessageRendererFactory;
    private JPanel mMainPanel;
    private JComponent mEmptyControls;
    private JPanel mControlsContainer;
    private JPanel mBodyContainer;
    private Message mCurrentMessage;
    private MessageRenderer mCurrentMessageRenderer;
    private DefaultMessageQueueViewerButtonsPanel mButtonsPanel;

    /**
     * Create the DefaultMessageQueueViewer given its factory.
     * @param factory this viewer's factory
     * @param rendererFactory the message renderer factory
     * @param applicationName the application name
     */
    public DefaultMessageQueueViewer(final MessageQueueViewerFactory factory,
            final MessageRendererFactory rendererFactory,
            final String applicationName) {
        super(factory);
        this.mMessageRendererFactory = rendererFactory;
        final Frame mainFrame = getMessageQueueViewerFactory().getMainFrame();
        mMessageQueue = getMessageQueueViewerFactory().getMessageQueue();
        
        mDialog = new JDialog(mainFrame, false);
        mDialog.setPreferredSize(new Dimension(mainFrame.getWidth() - 40, 200));
        // TODO I'd like it moved so it's bottom edge is just above the
        // status bar, and centred within the main app frame
        mDialog.setTitle("Messages from " + applicationName);
        
        initialiseNoMessagesTextPane();
        initialiseEmptyControlsPane();
        initialiseMainPanel();
        mDialog.add(mMainPanel);
        
        mDialog.addWindowListener(new MessageViewerCloseNotifier(getMessageQueueViewerFactory()));
        
        updateWithSelectedMessage();
        
        mDialog.pack();
        mDialog.setVisible(true);
    }

    private void initialiseEmptyControlsPane() {
        mEmptyControls = new JLabel("");
    }

    private void initialiseNoMessagesTextPane() {
        mNoMessagesTextPane = new HTMLPanel(createHTMLText("<p align=\"center\">No messages waiting</p>"));
    }

    private void setBodyComponent(final Component component) {
        LOGGER.debug("Setting body component to " + component);
        mBodyContainer.removeAll();
        mBodyContainer.add(component, BorderLayout.CENTER);
        mBodyContainer.revalidate();
        mBodyContainer.repaint();
    }

    private void setControlsComponent(final Component controls) {
        LOGGER.debug("Setting controls component to " + controls);
        mControlsContainer.removeAll();
        mControlsContainer.add(controls, BorderLayout.EAST);
        mControlsContainer.revalidate();
        mControlsContainer.repaint();
    }
    
    private void updateWithSelectedMessage() {
        final int currentIndex = mMessageQueue.getCurrentMessageIndex();
        boolean enablePrevious = false;
        boolean enableRemove = false;
        boolean enableNext = false;
        // TODO turn off updates
        if (currentIndex == -1) {
            mCurrentMessage = null;
            mCurrentMessageRenderer = null;
            LOGGER.info("empty - no message display");
            mMessageSubjectPane.setText(createDialogFontText("<em>No messages</em>"));
            setBodyComponent(mNoMessagesTextPane);
            setControlsComponent(mEmptyControls);
        } else {
            mCurrentMessage = mMessageQueue.getMessageByIndex(currentIndex);
            LOGGER.info("rendering message " + currentIndex + ": " + mCurrentMessage.getSubject());
            // Subject... always text, for all messages. so handle it here.
            mMessageSubjectPane.setText(createSubjectLabelText(currentIndex + 1, mMessageQueue.size(), mCurrentMessage.getSubject()));
            mCurrentMessageRenderer = mMessageRendererFactory.createRenderer(mCurrentMessage);
            setBodyComponent(mCurrentMessageRenderer.render());
            final Component controls = mCurrentMessageRenderer.renderControls();
            setControlsComponent(controls == null ? mEmptyControls : controls);
            enableRemove = true;
            enablePrevious = currentIndex != 0;
            enableNext = currentIndex != mMessageQueue.size() - 1;
        }
        mMessageSubjectPane.setCaretPosition(0);
        mButtonsPanel.setControlsEnabled(enablePrevious, enableRemove, enableNext);
        mMainPanel.validate();
        // TODO turn on updates
    }

    private String createHTMLText(final String bodyText) {
        return String.format("<html><head><title>sample text</title></head><body>%s</body></html>", bodyText);
    }

    private String createDialogFontText(final String input) {
        return String.format("<font face=\"dialog\">%s</font>", input);
    }

    private String createSubjectLabelText(final int messageNumber, final int numberOfMessages, final String subject) {
        return createDialogFontText(String.format("<b>Message %d of %d</b> - <em>%s</em>", messageNumber, numberOfMessages, subject));
    }

    /**
     * @return
     */
    private void initialiseMainPanel() {
        mMainPanel = new JPanel();
        mMainPanel.setLayout(new BorderLayout());
        mMainPanel.add(subjectPanel(), BorderLayout.NORTH);
        mBodyContainer = new JPanel();
        mBodyContainer.setLayout(new BorderLayout());
        mMainPanel.add(bodyPanel(), BorderLayout.CENTER);
        mButtonsPanel = buttonsPanel();
        mControlsContainer = new JPanel();
        mControlsContainer.setLayout(new BorderLayout());
        mMainPanel.add(controlsPanel(), BorderLayout.SOUTH);
    }

    private Component subjectPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JPanel greypanel = new JPanel();
        greypanel.setLayout(new BorderLayout());
        greypanel.setBackground(Color.GRAY);
        greypanel.setForeground(Color.WHITE);
        mMessageSubjectPane = new HTMLPanel();
        mMessageSubjectPane.setHTMLText(createHTMLText("<font face=\"dialog\"><b>No messages</b></font>"));
        mMessageSubjectPane.setBackground(Color.GRAY);
        mMessageSubjectPane.setForeground(Color.WHITE);
        greypanel.add(mMessageSubjectPane, BorderLayout.WEST);
        panel.add(greypanel, BorderLayout.CENTER);
        return panel;
    }

    private Component bodyPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(mBodyContainer), BorderLayout.CENTER);
        return panel;
    }

    private JPanel controlsPanel() {
        final JPanel controlsMainPanel = new JPanel();
        controlsMainPanel.setLayout(new BorderLayout());
        
        // Dynamic controls
        controlsMainPanel.add(mControlsContainer, BorderLayout.CENTER);
        
        // Static controls
        controlsMainPanel.add(mButtonsPanel, BorderLayout.EAST);
       
        return controlsMainPanel;
    }

    private DefaultMessageQueueViewerButtonsPanel buttonsPanel() {
        final DefaultMessageQueueViewerButtonsPanel buttonsPanel = new DefaultMessageQueueViewerButtonsPanel();

        buttonsPanel.addPreviousActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                mMessageQueue.previous();
                updateWithSelectedMessage();
            }
        });
        
        buttonsPanel.addRemoveActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                assert mCurrentMessage != null;
                assert mCurrentMessageRenderer != null;
                // used to be messageQueue.getMessageByIndex(messageQueue.getCurrentMessageIndex())
                mMessageQueue.removeMessage(mCurrentMessage);
                updateWithSelectedMessage();
            }
        });
        
        buttonsPanel.addNextActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                mMessageQueue.next();
                updateWithSelectedMessage();
            }
        });
        return buttonsPanel;
    }
}

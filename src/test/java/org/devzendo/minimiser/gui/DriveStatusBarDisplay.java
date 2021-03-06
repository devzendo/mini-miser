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

package org.devzendo.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.Beautifier;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.DefaultCursorManager;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commoncode.logging.Logging;
import org.devzendo.minimiser.gui.dialog.dstamessage.DSTAMessageId;
import org.devzendo.minimiser.gui.messagequeueviewer.DefaultMessageQueueViewerFactory;
import org.devzendo.minimiser.messagequeue.BooleanFlagSettingMessage;
import org.devzendo.minimiser.messagequeue.Message;
import org.devzendo.minimiser.messagequeue.MessageQueue;
import org.devzendo.minimiser.messagequeue.MessageQueueBorderGuardFactory;
import org.devzendo.minimiser.messagequeue.SimpleDSTAMessage;
import org.devzendo.minimiser.messagequeue.SimpleMessage;
import org.devzendo.minimiser.messagequeue.StubMessageQueueMiniMiserPrefs;
import org.devzendo.minimiser.pluginmanager.DummyAppPluginRegistry;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.devzendo.minimiser.util.DelayedExecutor;


/**
 * @author matt
 *
 */
public final class DriveStatusBarDisplay {

    private static final Logger LOGGER = Logger
            .getLogger(DriveStatusBarDisplay.class);
    private final MessageQueue messageQueue;
    private int messageNumber;
    
    private final JFrame frame;
    private final DefaultMessageQueueViewerFactory messageQueueViewerFactory;

    private DriveStatusBarDisplay()  {
        frame = new JFrame("title");
        frame.setPreferredSize(new Dimension(600, 480));
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        
        final JButton addMessageButton = new JButton("Add simple message");
        addMessageButton.addActionListener(new ActionListener () {
            public void actionPerformed(final ActionEvent e) {
                final Message.Importance importance = randomImportance();
                final String subject = "Message # " + ++messageNumber;
                final String content = "here is a sample document in HTML<br>"
                    + "it can have <b>bold</b> text and <em>italic</em> text<br>"
                    + "and to show off long documents, it has <br>"
                    + "many lines<br>"
                    + "of rather uninteresting text<br>"
                    + "before ending the document<br>"
                    + "but nevertheless, there are enough lines<br>"
                    + "and new paragraphs...<p>"
                    + "to make it long enough<br>"
                    + "to make the scrollbar active.";
                
                messageQueue.addMessage(new SimpleMessage(subject, content, importance));
            }
        });
        
        buttonPanel.add(addMessageButton);
        
        final JButton addDSTAMessageButton = new JButton("Add DSTA message");
        addDSTAMessageButton.addActionListener(new ActionListener () {
            public void actionPerformed(final ActionEvent e) {
                final Message.Importance importance = randomImportance();
                final String subject = "Upgrade <b>now!!!</b>";
                final String content = "Here is a sample message\nIt's a multiline message\n"
                    + "But otherwise, rather boring...";
                messageQueue.addMessage(new SimpleDSTAMessage(subject, content, importance, DSTAMessageId.TEST));
            }
        });
        
        buttonPanel.add(addDSTAMessageButton);

        final JButton addBooleanFlagMessageButton = new JButton("Add BooleanFlag message");
        addBooleanFlagMessageButton.addActionListener(new ActionListener () {
            public void actionPerformed(final ActionEvent e) {
                final Message.Importance importance = randomImportance();
                final String subject = "Please decide whether you want update checks";
                final String content = "The framework can check periodically for software updates.<br>"
                + "This requires an active Internet connection.<br>"
                + "Only the latest software details are obtained from our web site.<br>"
                + "No personal information is sent, other than your computer's IP address.<br>"
                + "Your IP address is logged by our ISP, but is not used by us to identify you.";
                final String checkboxText = "Allow periodic software update checks?";
                messageQueue.addMessage(
                    new BooleanFlagSettingMessage(subject,
                                                  content,
                                                  importance,
                                                  CoreBooleanFlags.UPDATE_CHECK_ALLOWED,
                                                  checkboxText));
            }
        });
        
        buttonPanel.add(addBooleanFlagMessageButton);

        final JButton removeMessageButton = new JButton("Remove first message");
        removeMessageButton.addActionListener(new ActionListener () {
            public void actionPerformed(final ActionEvent e) {
                if (messageQueue.size() > 0) {
                    messageQueue.removeMessage(messageQueue.getMessageByIndex(0));
                }
            }
        });
        
        buttonPanel.add(removeMessageButton);
        
        frame.add(buttonPanel, BorderLayout.NORTH);

        messageQueue = new MessageQueue(new MessageQueueBorderGuardFactory(new StubMessageQueueMiniMiserPrefs()));
        
        final MainFrameStatusBar mainFrameStatusBar = new MainFrameStatusBar(new DelayedExecutor());
        frame.add(mainFrameStatusBar.getPanel(), BorderLayout.SOUTH);

        final CursorManager cursorManager = new DefaultCursorManager();
        cursorManager.setMainFrame(frame);
        
        final PluginRegistry pluginRegistry = new DummyAppPluginRegistry();
        messageQueueViewerFactory = new DefaultMessageQueueViewerFactory(mainFrameStatusBar,
            frame, messageQueue, cursorManager, pluginRegistry);
        
        frame.pack();
        frame.setVisible(true);
        
        
        new StatusBarMessageQueueAdapter(mainFrameStatusBar, messageQueue, messageQueueViewerFactory).wireAdapter();
    }

    private Message.Importance randomImportance() {
        final double imprandom = Math.random();
        Message.Importance importance;
        if (imprandom < 0.3) {
            importance = Message.Importance.HIGH;
        } else if (imprandom < 0.6) {
            importance = Message.Importance.MEDIUM;
        } else {
            importance = Message.Importance.LOW;
        }
        return importance;
    }

    /**
     * @param args command line
     */
    public static void main(final String[] args) {
        BasicConfigurator.configure();
        List<String> argList = new ArrayList<String>(Arrays.asList(args));
        argList = Logging.getInstance().setupLoggingFromArgs(argList);
        

        GUIUtils.runOnEventThread(new Runnable() {

            public void run() {
                LOGGER.info("Message queue display experimentation starting...");
                Beautifier.makeBeautiful();
                new DriveStatusBarDisplay();
            }
        });
    }
}

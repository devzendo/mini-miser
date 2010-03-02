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

package org.devzendo.minimiser.gui.console.output;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.commoncode.gui.GUIUtils;


/**
 * An OutputConsole that uses a TextArea to logs its information.
 * <p>
 * It uses a background thread to ensure that constant updates to the JTextArea
 * are batched rather than drip-fed.
 * 
 * @author matt
 *
 */
public final class TextAreaOutputConsole implements OutputConsole {
    private int textAreaContentLength = 0;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    
    private Object lock;
    private StringBuilder updateText;
    private boolean bAlive;
    
    /**
     * Construct a text area-based OutputConsole 
     */
    public TextAreaOutputConsole() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, textArea.getFont().getSize()));
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);

        updateText = new StringBuilder();
        startUpdateThread();
    }

    private void startUpdateThread() {
        bAlive = true;
        lock = new Object();
        final Thread updateThread = new Thread(new Runnable() {

            public void run() {
                while (bAlive) {
                    synchronized (lock) {
                        if (updateText.length() > 0) {
                            final String out = updateText.toString();
                            updateText = new StringBuilder();
                            GUIUtils.invokeLaterOnEventThread(new Runnable() {
                                public void run() {
                                    textArea.append(out);
                                    textAreaContentLength += out.length();
                                    textArea.setCaretPosition(textAreaContentLength);
                                }
                            });
                        }
                    }
                    ThreadUtils.waitNoInterruption(100);
                }
            }
            
        });
        updateThread.setDaemon(true);
        updateThread.setName("Console updater");
        updateThread.start();
    }

    /**
     * Free all resources (i.e. the update thread) 
     */
    public void finished() {
        bAlive = false;
    }
    
    /**
     * Obtain the text area used by this OutputConsole
     * @return the JTextArea in a JScrollPane
     */
    public JComponent getTextArea() {
        return scrollPane;
    }

    /**
     * {@inheritDoc}
     */
    public void debug(final Object obj) {
        if (obj != null) {
            appendText(obj.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void error(final Object obj) {
        if (obj != null) {
            appendText(obj.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void info(final Object obj) {
        if (obj != null) {
            appendText(obj.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void warn(final Object obj) {
        if (obj != null) {
            appendText(obj.toString());
        }
    }
    
    private void appendText(final String text) {
        if (text.length() > 0) {
            synchronized (lock) {
                updateText.append(text);
                if (!text.endsWith("\n")) {
                    updateText.append("\n");
                }
            }
        }
    }
}

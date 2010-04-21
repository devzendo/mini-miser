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

package org.devzendo.minimiser.gui.tab.impl;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.devzendo.minimiser.gui.tab.Tab;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;

/**
 * A do-very-little, non-TDD Demo tab
 * 
 * @author matt
 *
 */
public final class DemoTab implements Tab {
    private final DatabaseDescriptor databaseDescriptor;
    private volatile JPanel panel;

    /**
     * Create the demo tab given a database descriptor 
     * @param descriptor the database descriptor
     */
    public DemoTab(final DatabaseDescriptor descriptor) {
        this.databaseDescriptor = descriptor;
    }

    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return panel;
    }

    /**
     * {@inheritDoc}
     */
    public void initComponent() {
        panel = new JPanel();
        panel.add(new JButton("This is a demo tab for the " + databaseDescriptor.getDatabaseName() + " database"));
    }
    
    /**
     * {@inheritDoc}
     */
    public void destroy() {
        // TODO: Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}
     */
    public void disposeComponent() {
        // TODO: Auto-generated method stub
        
    }
}

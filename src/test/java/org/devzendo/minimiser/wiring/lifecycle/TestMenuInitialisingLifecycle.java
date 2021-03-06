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

package org.devzendo.minimiser.wiring.lifecycle;

import javax.swing.JFrame;

import org.devzendo.commonapp.gui.MainFrameFactory;
import org.devzendo.commonapp.spring.springloader.ApplicationContext;
import org.devzendo.minimiser.gui.menu.MenuMediatorUnittestCase;
import org.junit.Assert;
import org.junit.Test;



/**
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/gui/menu/TestMenuInitialisingLifecycle.xml")
public final class TestMenuInitialisingLifecycle extends MenuMediatorUnittestCase {

    /**
     *
     */
    @Test
    public void menuIsInitialisedAfterConstruction() {
        final MainFrameFactory mainFrameFactory = getSpringLoader().getBean("&mainFrame", MainFrameFactory.class);
        mainFrameFactory.setMainFrame(new JFrame());

        // Start lifecycle (manually)
        final MenuInitialisingLifecycle lifecycle = new MenuInitialisingLifecycle(getSpringLoader());
        lifecycle.startup();

        // Menu should have been initialised by the lifecycle
        Assert.assertTrue(getStubMenu().isInitialised());
    }
}

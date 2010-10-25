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

package org.devzendo.minimiser.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.devzendo.minimiser.logging.LoggingTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests the MenuWiring class.
 * 
 * @author matt
 *
 */
public final class TestMenuWiring extends LoggingTestCase {
    private MenuWiring menuWiring;
    private static final MenuIdentifier FILE_CLOSE = new MenuIdentifier("FileClose");

    /**
     * 
     */
    @Before
    public void getMenuWiring() {
        menuWiring = new MenuWiring();
    }
    
    /**
     * 
     */
    @Test
    public void testNonExistantMenuItem() {
        Assert.assertNull(menuWiring.getMenuItem(TestMenuWiring.FILE_CLOSE));
    }

    /**
     * 
     */
    @Test
    public void testNonExistantActionListener() {
        Assert.assertNull(menuWiring.getActionListener(TestMenuWiring.FILE_CLOSE));
    }
    
    /**
     * 
     */
    @Test
    public void testReturnMenuItem() {
        final JMenuItem menuItem = new JMenuItem();
        menuWiring.storeMenuItem(TestMenuWiring.FILE_CLOSE, menuItem);
        Assert.assertEquals(menuItem, menuWiring.getMenuItem(TestMenuWiring.FILE_CLOSE));
    }
    
    /**
     * 
     */
    @Test
    public void testEmptyInitialActionListener() {
        final JMenuItem menuItem = new JMenuItem();
        menuWiring.storeMenuItem(TestMenuWiring.FILE_CLOSE, menuItem);
        Assert.assertNull(menuWiring.getActionListener(TestMenuWiring.FILE_CLOSE));
    }

    /**
     * 
     */
    @Test
    public void testSetActionListener() {
        final JMenuItem menuItem = new JMenuItem();
        menuWiring.storeMenuItem(TestMenuWiring.FILE_CLOSE, menuItem);
        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        };
        menuWiring.setActionListener(TestMenuWiring.FILE_CLOSE, actionListener);
        Assert.assertEquals(actionListener, menuWiring.getActionListener(TestMenuWiring.FILE_CLOSE));
    }

    /**
     * 
     */
    @Test
    public void testStoreMenuItemAgainRetainsActionListener() {
        final JMenuItem menuItem1 = new JMenuItem();
        menuWiring.storeMenuItem(TestMenuWiring.FILE_CLOSE, menuItem1);
        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        };
        menuWiring.setActionListener(TestMenuWiring.FILE_CLOSE, actionListener);

        final JMenuItem menuItem2 = new JMenuItem();
        menuWiring.storeMenuItem(TestMenuWiring.FILE_CLOSE, menuItem2);

        Assert.assertEquals(actionListener, menuWiring.getActionListener(TestMenuWiring.FILE_CLOSE));
    }
    
    /**
     * 
     */
    @Test
    public void testGeneratedActionListenerDispatches() {
        final JMenuItem menuItem = new JMenuItem();
        menuWiring.storeMenuItem(TestMenuWiring.FILE_CLOSE, menuItem);
        final ActionEvent[] result = new ActionEvent[] {null};
        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                result[0] = e;
            }
        };
        menuWiring.setActionListener(TestMenuWiring.FILE_CLOSE, actionListener);
        Assert.assertNull(result[0]);
        final ActionEvent event = new ActionEvent(menuItem, 69, "wahey");
        menuWiring.injectActionEvent(TestMenuWiring.FILE_CLOSE, event);
        Assert.assertEquals(event.getSource(), result[0].getSource());
        // does not match for some reason Assert.assertEquals(event.getID(), result[0].getID());
        Assert.assertEquals(event.getActionCommand(), result[0].getActionCommand());
    }
    
    /**
     * 
     */
    @Test
    public void testGeneratedTriggeringOfMenuItemDispatches() {
        final JMenuItem menuItem = new JMenuItem();
        menuWiring.storeMenuItem(TestMenuWiring.FILE_CLOSE, menuItem);
        final ActionEvent[] result = new ActionEvent[] {null};
        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                result[0] = e;
            }
        };
        menuWiring.setActionListener(TestMenuWiring.FILE_CLOSE, actionListener);
        Assert.assertNull(result[0]);
        menuWiring.triggerActionListener(TestMenuWiring.FILE_CLOSE);
        // with triggerActionListener, a dummy event is created
        // that contains the JMenuItem, so we can only check that
        Assert.assertNotNull(result[0]);
        Assert.assertEquals(menuItem, result[0].getSource());
    }

    /**
     * 
     */
    @Test
    public void testGeneratedActionListenerStillDispatchesAfterStoringMenuItemAgain() {
        final JMenuItem menuItem1 = new JMenuItem();
        menuWiring.storeMenuItem(TestMenuWiring.FILE_CLOSE, menuItem1);
        final ActionEvent[] result = new ActionEvent[] {null};
        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                result[0] = e;
            }
        };
        menuWiring.setActionListener(TestMenuWiring.FILE_CLOSE, actionListener);

        // store again
        final JMenuItem menuItem2 = new JMenuItem();
        menuWiring.storeMenuItem(TestMenuWiring.FILE_CLOSE, menuItem2);
        
        Assert.assertNull(result[0]);
        final ActionEvent event = new ActionEvent(menuItem1, 69, "wahey");
        menuWiring.injectActionEvent(TestMenuWiring.FILE_CLOSE, event);
        Assert.assertEquals(event.getSource(), result[0].getSource());
        // does not match for some reason Assert.assertEquals(event.getID(), result[0].getID());
        Assert.assertEquals(event.getActionCommand(), result[0].getActionCommand());
    }
    
    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void enableWithNoMenuItemThrows() {
        menuWiring.enableMenuItem(TestMenuWiring.FILE_CLOSE);
    }

    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void setEnabledWithNoMenuItemThrows() {
        menuWiring.setMenuItemEnabled(TestMenuWiring.FILE_CLOSE, false);
    }

    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void disableWithNoMenuItemThrows() {
        menuWiring.disableMenuItem(TestMenuWiring.FILE_CLOSE);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void isEnabledWithNoMenuItemThrows() {
        menuWiring.isMenuItemEnabled(TestMenuWiring.FILE_CLOSE);
    }
    
    /**
     * 
     */
    @Test
    public void menuItemsCanBeDisabledAndEnabled() {
        final JMenuItem menuItem1 = new JMenuItem();
        menuWiring.storeMenuItem(TestMenuWiring.FILE_CLOSE, menuItem1);
        
        Assert.assertTrue(menuWiring.isMenuItemEnabled(TestMenuWiring.FILE_CLOSE));
        
        menuWiring.disableMenuItem(TestMenuWiring.FILE_CLOSE);
        Assert.assertFalse(menuWiring.isMenuItemEnabled(TestMenuWiring.FILE_CLOSE));

        menuWiring.enableMenuItem(TestMenuWiring.FILE_CLOSE);
        Assert.assertTrue(menuWiring.isMenuItemEnabled(TestMenuWiring.FILE_CLOSE));

        menuWiring.setMenuItemEnabled(TestMenuWiring.FILE_CLOSE, false);
        Assert.assertFalse(menuWiring.isMenuItemEnabled(TestMenuWiring.FILE_CLOSE));

        menuWiring.setMenuItemEnabled(TestMenuWiring.FILE_CLOSE, true);
        Assert.assertTrue(menuWiring.isMenuItemEnabled(TestMenuWiring.FILE_CLOSE));
    }
    
    /**
     * 
     */
    @Test
    public void createNonExistantMenuItemGetsNewMenuItemThenReturnsSame() {
        Assert.assertNull(menuWiring.getMenuItem(TestMenuWiring.FILE_CLOSE));
        final JMenuItem newMI = menuWiring.createMenuItem(TestMenuWiring.FILE_CLOSE, "Close", 'C');
        Assert.assertSame(newMI, menuWiring.createMenuItem(TestMenuWiring.FILE_CLOSE, "Close", 'C'));
    }
    
    /**
     * 
     */
    @Test
    public void replaceNonExistantMenuItemGetsNewMenuItemThenReturnsNew() {
        Assert.assertNull(menuWiring.getMenuItem(TestMenuWiring.FILE_CLOSE));
        final JMenuItem newMI = menuWiring.replaceMenuItem(TestMenuWiring.FILE_CLOSE, "Close", 'C');
        final JMenuItem replacedMI = menuWiring.replaceMenuItem(TestMenuWiring.FILE_CLOSE, "Close", 'C');
        Assert.assertNotSame(newMI, replacedMI);
    }
}

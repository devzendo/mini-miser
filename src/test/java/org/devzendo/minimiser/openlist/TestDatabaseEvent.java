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

package org.devzendo.minimiser.openlist;

import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Tests for equality and inequality of database events.
 * 
 * @author matt
 *
 */
public final class TestDatabaseEvent {
    private static final DatabaseDescriptor ONE = new DatabaseDescriptor("one");
    private static final DatabaseDescriptor TWO = new DatabaseDescriptor("two");
    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

    /**
     * 
     */
    @Test
    public void openedEquality() {
        Assert.assertTrue(new DatabaseOpenedEvent(ONE).equals(new DatabaseOpenedEvent(ONE)));
    }
    
    /**
     * 
     */
    @Test
    public void openedInequality() {
        Assert.assertFalse(new DatabaseOpenedEvent(ONE).equals(new DatabaseOpenedEvent(TWO)));
    }
    
    /**
     * 
     */
    @Test
    public void closedEquality() {
        Assert.assertTrue(new DatabaseClosedEvent(ONE).equals(new DatabaseClosedEvent(ONE)));
    }

    /**
     * 
     */
    @Test
    public void closedInequality() {
        Assert.assertFalse(new DatabaseClosedEvent(ONE).equals(new DatabaseClosedEvent(TWO)));
    }

    /**
     * 
     */
    @Test
    public void switchedEquality() {
        Assert.assertTrue(new DatabaseSwitchedEvent(ONE).equals(new DatabaseSwitchedEvent(ONE)));
    }

    /**
     * 
     */
    @Test
    public void switchedInequality() {
        Assert.assertFalse(new DatabaseSwitchedEvent(ONE).equals(new DatabaseSwitchedEvent(TWO)));
    }

    /**
     * 
     */
    @Test
    public void openedInequalClosed() {
        Assert.assertFalse(new DatabaseOpenedEvent(ONE).equals(new DatabaseClosedEvent(ONE)));
    }
    
    /**
     * 
     */
    @Test
    public void openedInequalSwitched() {
        Assert.assertFalse(new DatabaseOpenedEvent(ONE).equals(new DatabaseSwitchedEvent(ONE)));
    }
    
    /**
     * 
     */
    @Test
    public void closeedInequalSwitched() {
        Assert.assertFalse(new DatabaseClosedEvent(ONE).equals(new DatabaseSwitchedEvent(ONE)));
    }
}

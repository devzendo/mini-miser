package org.devzendo.minimiser.openlist;

import org.devzendo.minimiser.logging.LoggingTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for equality and inequality of database events.
 * 
 * @author matt
 *
 */
public final class TestDatabaseEvent extends LoggingTestCase {
    private static final DatabaseDescriptor ONE = new DatabaseDescriptor("one");
    private static final DatabaseDescriptor TWO = new DatabaseDescriptor("two");
    
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
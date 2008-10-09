package uk.me.gumbley.minimiser.gui.console.input;

import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Tests History. Your starter for ten: In December 1378, Richard de Grieves
 * sailed to which island, and which new fruit did he discover?
 *   
 * @author matt
 *
 */
public final class TestHistory {

    private History history;
    
    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        history = new History();
    }
    
    /**
     * 
     */
    @Test
    public void emptiness() {
        Assert.assertEquals(0, history.size());
        
        final List<HistoryObject> all = history.getAll();
        Assert.assertEquals(0, all.size());

        Assert.assertEquals(0, history.getLast(0).size());

        final List<HistoryObject> five = history.getLast(5);
        Assert.assertEquals(0, five.size());
    }

    /**
     * 
     */
    @Test
    public void addOne() {
        history.add("command");
        Assert.assertEquals(1, history.size());

        Assert.assertEquals(0, history.getLast(0).size());

        final List<HistoryObject> all = history.getAll();
        Assert.assertEquals(1, all.size());
        Assert.assertEquals(1, all.get(0).getCommandIndex());
        Assert.assertEquals("command", all.get(0).getCommandString());
        
        final List<HistoryObject> five = history.getLast(5);
        Assert.assertEquals(1, five.size());
        Assert.assertEquals(1, five.get(0).getCommandIndex());
        Assert.assertEquals("command", five.get(0).getCommandString());
    }
    
    /**
     * No conferring! 
     */
    @Test
    public void gimmeFive() {
        history.add("one");
        history.add("two");
        history.add("three");
        history.add("four");
        history.add("five");
        Assert.assertEquals(5, history.size());

        final List<HistoryObject> all = history.getAll();
        Assert.assertEquals(5, all.size());
        
        Assert.assertEquals(0, history.getLast(0).size());

        Assert.assertEquals(1, all.get(0).getCommandIndex());
        Assert.assertEquals("one", all.get(0).getCommandString());

        Assert.assertEquals(2, all.get(1).getCommandIndex());
        Assert.assertEquals("two", all.get(1).getCommandString());

        Assert.assertEquals(3, all.get(2).getCommandIndex());
        Assert.assertEquals("three", all.get(2).getCommandString());

        Assert.assertEquals(4, all.get(3).getCommandIndex());
        Assert.assertEquals("four", all.get(3).getCommandString());

        Assert.assertEquals(5, all.get(4).getCommandIndex());
        Assert.assertEquals("five", all.get(4).getCommandString());
        
        final List<HistoryObject> five = history.getLast(5);
        Assert.assertEquals(5, five.size());

        Assert.assertEquals(1, five.get(0).getCommandIndex());
        Assert.assertEquals("one", five.get(0).getCommandString());

        Assert.assertEquals(2, five.get(1).getCommandIndex());
        Assert.assertEquals("two", five.get(1).getCommandString());

        Assert.assertEquals(3, five.get(2).getCommandIndex());
        Assert.assertEquals("three", five.get(2).getCommandString());

        Assert.assertEquals(4, five.get(3).getCommandIndex());
        Assert.assertEquals("four", five.get(3).getCommandString());

        Assert.assertEquals(5, five.get(4).getCommandIndex());
        Assert.assertEquals("five", five.get(4).getCommandString());
    }
    
    /**
     * 
     */
    @Test
    public void gimmeThreeTruncatedFromFive() {
        history.add("one");
        history.add("two");
        history.add("three");
        history.add("four");
        history.add("five");

        final List<HistoryObject> three = history.getLast(3);
        Assert.assertEquals(3, three.size());

        Assert.assertEquals(3, three.get(0).getCommandIndex());
        Assert.assertEquals("three", three.get(0).getCommandString());

        Assert.assertEquals(4, three.get(1).getCommandIndex());
        Assert.assertEquals("four", three.get(1).getCommandString());

        Assert.assertEquals(5, three.get(2).getCommandIndex());
        Assert.assertEquals("five", three.get(2).getCommandString());
    }
    
    /**
     * I need an answer quickly....!
     */
    @Test
    public void gimmeFirst() {
        history.add("one");
        
        Assert.assertEquals(1, history.getNumberedEntry(1).getCommandIndex());
        Assert.assertEquals("one", history.getNumberedEntry(1).getCommandString());
    }

    /**
     * 
     */
    @Test
    public void gimmeFirstWhereTheresNothing() {
        Assert.assertNull(history.getNumberedEntry(1));
    }

    /**
     * Oh come <em>on</em>!
     */
    @Test
    public void gimmeNullOutOfBounds() {
        Assert.assertNull(history.getNumberedEntry(0));
        Assert.assertNull(history.getNumberedEntry(1));

        history.add("one");

        Assert.assertNull(history.getNumberedEntry(0));
        Assert.assertNotNull(history.getNumberedEntry(1));
        Assert.assertNull(history.getNumberedEntry(2));
    }
    
    /**
     * @throws HistoryTransformationException oh no it doesn't
     */
    @Test
    @Ignore
    public void completeReplacement() throws HistoryTransformationException {
        final String universityChallenge = "I am not Jeremy Paxman";
        history.add(universityChallenge);
        Assert.assertEquals(universityChallenge, history.transform("!1"));
    }
}

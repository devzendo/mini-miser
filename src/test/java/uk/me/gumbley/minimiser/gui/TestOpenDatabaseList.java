package uk.me.gumbley.minimiser.gui;

import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Test the open database list
 * 
 * @author matt
 *
 */
public final class TestOpenDatabaseList extends LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestOpenDatabaseList.class);
    private OpenDatabaseList list;

    /**
     * Get the ODL
     */
    @Before
    public void getOpenDatabaseList() {
        list = new OpenDatabaseList();
    }
    
    /**
     * Empty ODLs have no databases
     */
    @Test
    public void testEmptiness() {
        final int numberOfDatabases = list.getNumberOfDatabases();
        LOGGER.info("There are " + numberOfDatabases + " open databases");
        //Assert.assertEquals(0, numberOfDatabases); // inexplicably fails?!
        Assert.assertTrue(numberOfDatabases == 0);
        Assert.assertNull(list.getCurrentDatabase());
    }
    
    /**
     * Open a database, a listener is fired
     */
    @SuppressWarnings("unchecked")
    @Test
    public void openDatabaseFiresListener() {
        final Observer<DatabaseEvent> obs = EasyMock.createMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseOpenedEvent("testdb")));
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent("testdb")));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);
        
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("testdb");
        list.addOpenedDatabase(databaseDescriptor);

        EasyMock.verify(obs);
    }

    /**
     * Open a database, it's made current
     */
    @SuppressWarnings("unchecked")
    @Test
    public void openDatabaseMakesItCurrent() {
        final DatabaseDescriptor one = new DatabaseDescriptor("one");
        list.addOpenedDatabase(one);
        Assert.assertEquals(one, list.getCurrentDatabase());
        Assert.assertTrue(1 == list.getNumberOfDatabases());
        
        final DatabaseDescriptor two = new DatabaseDescriptor("two");
        list.addOpenedDatabase(two);
        Assert.assertEquals(two, list.getCurrentDatabase());
        Assert.assertTrue(2 == list.getNumberOfDatabases());
        
        final DatabaseDescriptor three = new DatabaseDescriptor("three");
        list.addOpenedDatabase(three);
        Assert.assertEquals(three, list.getCurrentDatabase());
        Assert.assertTrue(3 == list.getNumberOfDatabases());
    }

    /**
     * Open a database, a lister that was attached but now removed isn't
     * fired.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void openDatabaseDoesntFireRemovedListener() {
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseOpenedEvent("testdb")));
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent("testdb")));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);
        
        list.addOpenedDatabase(new DatabaseDescriptor("testdb"));
        
        list.removeDatabaseEventObserver(obs);
        
        list.addOpenedDatabase(new DatabaseDescriptor("securedb"));
        
        EasyMock.verify(obs);
    }
    
    /**
     * Adding a database that already exists just switches to it
     */
    @SuppressWarnings("unchecked")
    @Test
    public void addExistingDatabase() {
        list.addOpenedDatabase(new DatabaseDescriptor("one"));
        Assert.assertTrue(1 == list.getNumberOfDatabases());
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent("one")));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);

        list.addOpenedDatabase(new DatabaseDescriptor("one"));
        Assert.assertTrue(1 == list.getNumberOfDatabases());
        
        EasyMock.verify(obs);
    }
    
    /**
     * Can't remove a database that isn't there
     */
    @Test(expected = IllegalStateException.class)
    public void removeNonexistantDatabase() {
        list.removeClosedDatabase(new DatabaseDescriptor("bassssooon!"));
    }
    
    /**
     * If you have multiple databases open, and you have currently selected
     * the one in the middle of the list (the current tick is on the middle
     * one), then closing that database should switch to the next database,
     * i.e. the tick should stay in the same place.
     * 
     */
    
    /**
     * Close the only database, fire a listener, and get list empty.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void closeOnlyDatabaseFiresListener() {
        list.addOpenedDatabase(new DatabaseDescriptor("testdb"));
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseClosedEvent("testdb")));
        obs.eventOccurred(EasyMock.isA(DatabaseListEmptyEvent.class));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);
        
        list.removeClosedDatabase(new DatabaseDescriptor("testdb"));
        
        EasyMock.verify(obs);
    }

    /**
     * Close the penultimate database, fire a listener, and switch to the last
     * database.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void closePenultimateDatabaseFiresListener() {
        list.addOpenedDatabase(new DatabaseDescriptor("one"));
        list.addOpenedDatabase(new DatabaseDescriptor("two"));
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseClosedEvent("one")));
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent("two")));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);
        
        list.removeClosedDatabase(new DatabaseDescriptor("one"));
        
        EasyMock.verify(obs);
    }

    /**
     * Close the last database, fire a listener, and switch to the only
     * database.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void closeLastDatabaseFiresListener() {
        list.addOpenedDatabase(new DatabaseDescriptor("one"));
        list.addOpenedDatabase(new DatabaseDescriptor("two"));
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseClosedEvent("two")));
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent("one")));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);
        
        list.removeClosedDatabase(new DatabaseDescriptor("two"));
        
        EasyMock.verify(obs);
    }

    /**
     * Close a database, a listener that was attached but now removed isn't
     * fired.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void closeDatabaseDoesntFireRemovedListener() {
        list.addOpenedDatabase(new DatabaseDescriptor("testdb"));
        list.addOpenedDatabase(new DatabaseDescriptor("securedb"));
        
        final Observer<DatabaseEvent> obs = EasyMock.createMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseClosedEvent("testdb")));
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent("securedb")));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);

        list.removeClosedDatabase(new DatabaseDescriptor("testdb"));
        
        list.removeDatabaseEventObserver(obs);
        
        list.removeClosedDatabase(new DatabaseDescriptor("securedb"));
        
        EasyMock.verify(obs);
    }

    /**
     * The list of open databses should be consistent with what's added and
     * removed from the ODL.
     */
    @Test
    public void testListOfOpenDatabases() {
        Assert.assertEquals(Arrays.asList(new DatabaseDescriptor[] {}),
            list.getOpenDatabases());
        list.addOpenedDatabase(new DatabaseDescriptor("db1"));
        final List<DatabaseDescriptor> expectedList = Arrays.asList(new DatabaseDescriptor[] {
                        new DatabaseDescriptor("db1")
                    });
        Assert.assertEquals(expectedList, list.getOpenDatabases());

        final DatabaseDescriptor databaseDescriptorDb2 = new DatabaseDescriptor("db2");
        list.addOpenedDatabase(databaseDescriptorDb2);
        final List<DatabaseDescriptor> newExpectedList = Arrays.asList(new DatabaseDescriptor[] {
                new DatabaseDescriptor("db1"),
                databaseDescriptorDb2
            });
        Assert.assertEquals(newExpectedList, list.getOpenDatabases());

        list.removeClosedDatabase(databaseDescriptorDb2);
        Assert.assertEquals(expectedList, list.getOpenDatabases());
    }
}

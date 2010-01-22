package org.devzendo.minimiser.openlist;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.logging.LoggingTestCase;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


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
        Assert.assertEquals(0, numberOfDatabases); // inexplicably fails?!
        Assert.assertNull(list.getCurrentDatabase());
        Assert.assertFalse(list.containsDatabase(new DatabaseDescriptor("foo")));
    }
    
    /**
     * Adding null shouldn't fire listeners or increase list size.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void openingNullDoesNothing() {
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        EasyMock.replay(obs);
        
        list.addDatabaseEventObserver(obs);
        
        list.addOpenedDatabase(null);
        
        EasyMock.verify(obs);
        Assert.assertNull(list.getCurrentDatabase());
        Assert.assertFalse(list.containsDatabase(null));
    }
    
    /**
     * Open a database, a listener is fired
     */
    @SuppressWarnings("unchecked")
    @Test
    public void openDatabaseFiresListener() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("testdb");

        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseOpenedEvent(databaseDescriptor)));
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent(databaseDescriptor)));
        EasyMock.replay(obs);

        Assert.assertFalse(list.containsDatabase(databaseDescriptor));

        list.addDatabaseEventObserver(obs);
        
        list.addOpenedDatabase(databaseDescriptor);

        EasyMock.verify(obs);
        Assert.assertTrue(list.containsDatabase(databaseDescriptor));
    }

    /**
     * Open a database, it's made current
     */
    @Test
    public void openDatabaseMakesItCurrent() {
        final DatabaseDescriptor one = new DatabaseDescriptor("one");
        list.addOpenedDatabase(one);
        Assert.assertEquals(one, list.getCurrentDatabase());
        Assert.assertEquals(1, list.getNumberOfDatabases());
        
        final DatabaseDescriptor two = new DatabaseDescriptor("two");
        list.addOpenedDatabase(two);
        Assert.assertEquals(two, list.getCurrentDatabase());
        Assert.assertEquals(2, list.getNumberOfDatabases());
        
        final DatabaseDescriptor three = new DatabaseDescriptor("three");
        list.addOpenedDatabase(three);
        Assert.assertEquals(three, list.getCurrentDatabase());
        Assert.assertEquals(3, list.getNumberOfDatabases());
    }

    /**
     * Found in QA:
     * open two databases (two, then one), either by open or open recent
     * switch to the one on the top of the window menu (two)
     * then close it
     *   one should be the sole contents of the menu, but it's two
     *   - close is closing the wrong db - it's closing one, not two.
     *   - it's just getting the current database from the ODL
     *   
     *   The test is that switching should make the chosen database current.
     */
    @Test
    public void switchMakesCorrectDatabaseCurrent() {
        final DatabaseDescriptor one = new DatabaseDescriptor("one");
        final DatabaseDescriptor two = new DatabaseDescriptor("two");
        list.addOpenedDatabase(two);
        Assert.assertEquals("two", list.getCurrentDatabase().getDatabaseName());
        list.addOpenedDatabase(one);
        Assert.assertEquals("one", list.getCurrentDatabase().getDatabaseName());
        
        list.switchDatabase("two");
        Assert.assertEquals("two", list.getCurrentDatabase().getDatabaseName());
    }

    /**
     * 
     */
    @Test
    public void switchToExistingButNonCurrentShouldSwitch() {
        final DatabaseDescriptor one = new DatabaseDescriptor("one");
        final DatabaseDescriptor two = new DatabaseDescriptor("two");
        final DatabaseDescriptor three = new DatabaseDescriptor("three");
        
        list.addOpenedDatabase(three);
        Assert.assertEquals("three", list.getCurrentDatabase().getDatabaseName());
        list.addOpenedDatabase(two);
        Assert.assertEquals("two", list.getCurrentDatabase().getDatabaseName());
        list.addOpenedDatabase(one);
        Assert.assertEquals("one", list.getCurrentDatabase().getDatabaseName());
        
        list.switchDatabase("two");
        Assert.assertEquals("two", list.getCurrentDatabase().getDatabaseName());

        list.switchDatabase("one");
        Assert.assertEquals("one", list.getCurrentDatabase().getDatabaseName());
        
        list.switchDatabase("two");
        Assert.assertEquals("two", list.getCurrentDatabase().getDatabaseName());

        list.switchDatabase("three");
        Assert.assertEquals("three", list.getCurrentDatabase().getDatabaseName());

    }
    /**
     * Open a database, a lister that was attached but now removed isn't
     * fired.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void openDatabaseDoesntFireRemovedListener() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("testdb");
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseOpenedEvent(databaseDescriptor)));
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent(databaseDescriptor)));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);
        
        list.addOpenedDatabase(databaseDescriptor);
        
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
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        Assert.assertFalse(list.containsDatabase(databaseDescriptor));
        list.addOpenedDatabase(databaseDescriptor);
        Assert.assertEquals(1, list.getNumberOfDatabases());
        Assert.assertTrue(list.containsDatabase(databaseDescriptor));

        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent(databaseDescriptor)));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);

        list.addOpenedDatabase(databaseDescriptor);
        Assert.assertEquals(1, list.getNumberOfDatabases());
        Assert.assertTrue(list.containsDatabase(databaseDescriptor));
        
        EasyMock.verify(obs);
    }

    /**
     * Tests equality of DatabaseDescriptors, and containment within the open
     * database list.
     */
    @Test
    public void containsExistingInstance() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one", "/tmp/one");
        list.addOpenedDatabase(databaseDescriptor);
        Assert.assertTrue(list.containsDatabase(databaseDescriptor));
    }
    
    /**
     * Tests failure to contain. 
     */
    @Test
    public void doesNotContain() {
        list.addOpenedDatabase(new DatabaseDescriptor("one"));
        Assert.assertFalse(list.containsDatabase(new DatabaseDescriptor("two")));
    }
    
    /**
     * Tests equality of DatabaseDescriptors, and containment within the open
     * database list.
     */
    @Test
    public void containsAnotherInstanceOfSame() {
        list.addOpenedDatabase(new DatabaseDescriptor("one"));
        Assert.assertTrue(list.containsDatabase(new DatabaseDescriptor("one")));
        Assert.assertTrue(list.containsDatabase(new DatabaseDescriptor("one", "/tmp/one")));
        Assert.assertTrue(list.containsDatabase(new DatabaseDescriptor("one", "/tmp/other"))); // ignores path
    }

    /**
     * Removing null shouldn't fire listeners or decrease list size.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void removingNullDoesNothing() {
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        EasyMock.replay(obs);
        
        list.removeClosedDatabase(null);
        
        EasyMock.verify(obs);
        Assert.assertNull(list.getCurrentDatabase());
        Assert.assertFalse(list.containsDatabase(null));
    }
    
    /**
     * Can't remove a database that isn't there
     */
    @Test(expected = IllegalStateException.class)
    public void removeNonexistantDatabase() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("bassssooon!");
        Assert.assertFalse(list.containsDatabase(databaseDescriptor));
        list.removeClosedDatabase(databaseDescriptor);
        Assert.assertFalse(list.containsDatabase(databaseDescriptor));
    }
    
    /**
     * 
     */
    @Test
    public void addThenRemoveRemovesIt() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("bassssooon!");
        Assert.assertFalse(list.containsDatabase(databaseDescriptor));
        list.addOpenedDatabase(databaseDescriptor);
        Assert.assertTrue(list.containsDatabase(databaseDescriptor));
        list.removeClosedDatabase(databaseDescriptor);
        Assert.assertFalse(list.containsDatabase(databaseDescriptor));
    }
    
    /**
     * If you have multiple databases open, and you have currently selected
     * the one in the middle of the list (the current tick is on the middle
     * one), then closing that database should switch to the next database,
     * i.e. the tick should stay in the same place.
     * 
     */
    // TODO do we have a test that covers the above comment?
    
    /**
     * Close the only database, fire a listener, and get list empty.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void closeOnlyDatabaseFiresListener() {
        final DatabaseDescriptor testdb = new DatabaseDescriptor("testdb");
        list.addOpenedDatabase(testdb);
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseClosedEvent(testdb)));
        obs.eventOccurred(EasyMock.isA(DatabaseListEmptyEvent.class));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);

        Assert.assertTrue(list.containsDatabase(testdb));
        list.removeClosedDatabase(testdb);
        Assert.assertFalse(list.containsDatabase(testdb));
        
        EasyMock.verify(obs);
    }

    /**
     * Close the penultimate database, fire a listener, and switch to the last
     * database.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void closePenultimateDatabaseFiresListener() {
        final DatabaseDescriptor databaseDescriptorOne = new DatabaseDescriptor("one");
        list.addOpenedDatabase(databaseDescriptorOne);
        final DatabaseDescriptor databaseDescriptorTwo = new DatabaseDescriptor("two");
        list.addOpenedDatabase(databaseDescriptorTwo);
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseClosedEvent(databaseDescriptorOne)));
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent(databaseDescriptorTwo)));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);

        Assert.assertTrue(list.containsDatabase(databaseDescriptorOne));
        Assert.assertTrue(list.containsDatabase(databaseDescriptorTwo));
        list.removeClosedDatabase(databaseDescriptorOne);
        Assert.assertFalse(list.containsDatabase(databaseDescriptorOne));
        Assert.assertTrue(list.containsDatabase(databaseDescriptorTwo));
        
        EasyMock.verify(obs);
    }

    /**
     * Close the last database, fire a listener, and switch to the only
     * database.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void closeLastDatabaseFiresListener() {
        final DatabaseDescriptor one = new DatabaseDescriptor("one");
        list.addOpenedDatabase(one);
        final DatabaseDescriptor two = new DatabaseDescriptor("two");
        list.addOpenedDatabase(two);
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseClosedEvent(two)));
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent(one)));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);
        
        list.removeClosedDatabase(two);
        
        EasyMock.verify(obs);
    }

    /**
     * Close a database, a listener that was attached but now removed isn't
     * fired.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void closeDatabaseDoesntFireRemovedListener() {
        final DatabaseDescriptor testdb = new DatabaseDescriptor("testdb");
        list.addOpenedDatabase(testdb);
        final DatabaseDescriptor securedb = new DatabaseDescriptor("securedb");
        list.addOpenedDatabase(securedb);
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseClosedEvent(testdb)));
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent(securedb)));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);

        list.removeClosedDatabase(testdb);
        
        list.removeDatabaseEventObserver(obs);
        
        list.removeClosedDatabase(securedb);
        
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
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSwitchExistingDatabase() {
        list.addOpenedDatabase(new DatabaseDescriptor("testdb"));
        final DatabaseDescriptor securedb = new DatabaseDescriptor("securedb");
        list.addOpenedDatabase(securedb);
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent(securedb)));
        EasyMock.replay(obs);
        
        list.addDatabaseEventObserver(obs);

        list.switchDatabase("securedb");
        
        EasyMock.verify(obs);
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSwitchNonExistantDatabase() {
        list.addOpenedDatabase(new DatabaseDescriptor("testdb"));
        list.addOpenedDatabase(new DatabaseDescriptor("securedb"));
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        EasyMock.replay(obs);

        list.addDatabaseEventObserver(obs);

        list.switchDatabase("wazoo");
        
        EasyMock.verify(obs);
    }
    
    /**
     * Open a database, check for empty state, listener does not
     * receive the empty notification
     */
    @SuppressWarnings("unchecked")
    @Test
    public void openDatabaseThenCheckForEmptinessDoesNotFireListener() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("testdb");
        Assert.assertFalse(list.containsDatabase(databaseDescriptor));

        list.addOpenedDatabase(databaseDescriptor);
        
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);
        
        list.checkForEmptiness();

        EasyMock.verify(obs);
        Assert.assertTrue(list.containsDatabase(databaseDescriptor));
    }

    /**
     * Do not open any databasees, check for empty state, listener
     * does receive the empty notification
     */
    @SuppressWarnings("unchecked")
    @Test
    public void noDatabasesOpenOnCheckforEmptinessFiresListener() {
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.isA(DatabaseListEmptyEvent.class));
        EasyMock.replay(obs);
        list.addDatabaseEventObserver(obs);
        
        list.checkForEmptiness();

        EasyMock.verify(obs);
    }
}

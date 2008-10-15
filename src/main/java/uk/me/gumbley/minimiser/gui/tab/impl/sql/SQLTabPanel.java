package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.console.input.HistoryObject;
import uk.me.gumbley.minimiser.gui.console.input.InputConsoleEvent;
import uk.me.gumbley.minimiser.gui.console.input.InputConsoleEventError;
import uk.me.gumbley.minimiser.gui.console.input.TextAreaInputConsole;
import uk.me.gumbley.minimiser.gui.console.output.TextAreaOutputConsole;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * The SQL Tab's main panel. A developer-friendly (and moderately user-friendly,
 * if you know SQL) SQL investigation, debug and diagnosis tab.
 * <p>
 * Not much TDD here.
 * @author matt
 *
 */
@SuppressWarnings("serial")
public final class SQLTabPanel extends JPanel {
    private final DatabaseDescriptor databaseDescriptor;
    private final CursorManager cursorManager;
    private TextAreaOutputConsole outputConsole;
    private CommandProcessor commandProcessor; 
    private TextAreaInputConsole inputConsole;

    /**
     * Construct the SQL Tab main panel
     * @param descriptor the database descriptor
     * @param cursor the cursor manager
     */
    public SQLTabPanel(final DatabaseDescriptor descriptor, final CursorManager cursor) {
        super();
        databaseDescriptor = descriptor;
        cursorManager = cursor;

        this.setLayout(new BorderLayout());
        
        final JTabbedPane outputTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        this.add(outputTabbedPane, BorderLayout.CENTER);
        
        outputConsole = new TextAreaOutputConsole();
        outputTabbedPane.add(outputConsole.getTextArea(), "Console");
        // TODO I want the tabbedpane to go from top to the bottom - the complete
        // height, then below that, the input console. not quite there yet.

        final JTable table = new JTable(1, 1);
        final JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(table), BorderLayout.NORTH);
        outputTabbedPane.add(tablePanel, "Table");  // example!!

        //final TableDisplay multiTableDisplay = new MultiTableDisplay(new ConsoleTableDisplay(outputConsole), new TableConsoleDisplay(table));
        final TableDisplay consoleTableDisplay = new ConsoleTableDisplay(outputConsole);

        inputConsole = new TextAreaInputConsole();
        inputConsole.addInputConsoleEventListener(new InputConsoleObserver());
        this.add(inputConsole.getTextArea(), BorderLayout.SOUTH);
        
        final List<CommandHandler> commandHandlers = new ArrayList<CommandHandler>();
        commandHandlers.add(new HistoryCommandHandler());
        commandHandlers.add(new SQLCommandHandler(outputConsole, consoleTableDisplay, databaseDescriptor));
        commandProcessor = new CommandProcessor(outputConsole, commandHandlers);
    }

    
    /**
     * Process a line of input by passing it to CommandProcessor.
     * @param inputLine the line of input.
     */
    public void processInputLine(final String inputLine) {
        assert SwingUtilities.isEventDispatchThread();
        cursorManager.hourglass();
        new SwingWorker() {

            @Override
            public Object construct() {
                commandProcessor.processCommand(inputLine);
                return null;
            }
            
            public void finished() {
                cursorManager.normal();
            }
        } .start();
    }


    /**
     * Observes input lines, either reports them as errors in the output console
     * or passes them onto the InputProcessor.
     * 
     * @author matt
     *
     */
    private final class InputConsoleObserver implements
            Observer<InputConsoleEvent> {
        public void eventOccurred(final InputConsoleEvent observableEvent) {
            assert SwingUtilities.isEventDispatchThread();

            if (observableEvent instanceof InputConsoleEventError) {
                final InputConsoleEventError errorEvent = (InputConsoleEventError) observableEvent;
                logError(errorEvent);
                return;
            }
            final String inputLine = observableEvent.getInputLine();
            final int nextHistoryNumber = inputConsole.getNextHistoryNumber(); // hasn't been stashed in history yet 
            outputConsole.debug(nextHistoryNumber + " > " + inputLine);
            
            cursorManager.hourglass();
            new SwingWorker() {
            
                @Override
                public Object construct() {
                    commandProcessor.processCommand(inputLine);
                    return null;
                }
                
                public void finished() {
                    cursorManager.normal();
                }
            } .start();
        }

        private void logError(final InputConsoleEventError errorEvent) {
            outputConsole.warn(errorEvent.getInputLine());
            final String[] errors = errorEvent.getErrorLines();
            if (errors != null) {
                for (final String errorString : errors) {
                    outputConsole.warn(errorString);
                }
            }
        }
    }

    /**
     * Handles the history commands.
     * 
     * @author matt
     *
     */
    private final class HistoryCommandHandler implements CommandHandler {
        /**
         * {@inheritDoc}
         */
        public boolean handleCommand(final String command) {
            final String[] words = command.trim().split("\\s+");
            if (words == null || words.length == 0) {
                return false;
            }
            if (words[0].equalsIgnoreCase("history") || words[0].equalsIgnoreCase("h")) {
                List<HistoryObject> history;
                if (words.length > 1) {
                    try {
                        final int num = Integer.parseInt(words[1]);
                        history = inputConsole.getLastHistory(num); 
                    } catch (final NumberFormatException nfe) {
                        outputConsole.warn("'" + words[1] + "' is not numeric in history command");
                        return true; // don't pass on to anyone else
                    }
                } else {
                    history = inputConsole.getHistory();
                }
                if (history != null && history.size() > 0) {
                    for (final HistoryObject h : history) {
                        outputConsole.info(h.getCommandIndex() + " " + h.getCommandString());
                    }
                }
                return true;
            }
            return false;
        }
    }
}

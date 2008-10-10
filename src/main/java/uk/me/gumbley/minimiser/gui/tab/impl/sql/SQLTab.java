package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.console.input.HistoryObject;
import uk.me.gumbley.minimiser.gui.console.input.InputConsoleEvent;
import uk.me.gumbley.minimiser.gui.console.input.InputConsoleEventError;
import uk.me.gumbley.minimiser.gui.console.input.TextAreaInputConsole;
import uk.me.gumbley.minimiser.gui.console.output.TextAreaOutputConsole;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * A developer-friendly (and moderately user-friendly, if you know SQL) SQL
 * investigation, debug and diagnosis tab.
 * <p>
 * No TDD here.
 *  
 * @author matt
 *
 */
public final class SQLTab implements Tab {
    private final DatabaseDescriptor databaseDescriptor;
    private volatile JPanel mainPanel;
    private TextAreaOutputConsole outputConsole;
    private CommandProcessor commandProcessor;
    private TextAreaInputConsole inputConsole;

    /**
     * Construct the SQL tab
     * @param descriptor the database descriptor
     */
    public SQLTab(final DatabaseDescriptor descriptor) {
        databaseDescriptor = descriptor;
    }

    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return mainPanel;
    }

    /**
     * {@inheritDoc}
     */
    public void initComponent() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(8, 8));
                
        final JTabbedPane outputTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        mainPanel.add(outputTabbedPane, BorderLayout.NORTH);
        
        outputConsole = new TextAreaOutputConsole();
        outputTabbedPane.add(outputConsole.getTextArea(), "Console");
        // TODO I want the tabbedpane to go from top to the bottom - the complete
        // height, then below that, the input console. not quite there yet.

        outputTabbedPane.add(new JScrollPane(new JTable(20, 5)), "Table");  // example!!
        
        inputConsole = new TextAreaInputConsole();
        inputConsole.addInputConsoleEventListener(new InputConsoleObserver());
        mainPanel.add(inputConsole.getTextArea(), BorderLayout.SOUTH);
        
        final List<CommandHandler> commandHandlers = new ArrayList<CommandHandler>();
        commandHandlers.add(new HistoryCommandHandler());
        commandHandlers.add(new SQLCommandHandler(databaseDescriptor));
        commandProcessor = new CommandProcessor(outputConsole, commandHandlers);
    }

    
    /**
     * Process a line of input by passing it to CommandProcessor.
     * @param inputLine the line of input.
     */
    public void processInputLine(final String inputLine) {
        commandProcessor.processCommand(inputLine);
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * {@inheritDoc}
     */
    public void disposeComponent() {
        // TODO Auto-generated method stub
        
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
            if (observableEvent instanceof InputConsoleEventError) {
                final InputConsoleEventError errorEvent = (InputConsoleEventError) observableEvent;
                logError(errorEvent);
                return;
            }
            final String inputLine = observableEvent.getInputLine();
            //outputConsole.debug(inputLine);
            processInputLine(inputLine);
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

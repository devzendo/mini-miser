package uk.me.gumbley.minimiser.gui.tab.impl.sql;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.console.input.InputConsoleEvent;
import uk.me.gumbley.minimiser.gui.console.input.InputConsoleEventError;
import uk.me.gumbley.minimiser.gui.console.input.TextAreaInputConsole;
import uk.me.gumbley.minimiser.gui.console.output.TextAreaOutputConsole;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;
import uk.me.gumbley.minimiser.tabledisplay.TableDisplay;

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
    private final TextAreaOutputConsole outputConsole;
    private final CommandProcessor commandProcessor; 
    private final TextAreaInputConsole inputConsole;

    /**
     * Construct the SQL Tab main panel
     * @param descriptor the database descriptor
     * @param cursor the cursor manager
     * @param appDetails the application name and version
     */
    public SQLTabPanel(final DatabaseDescriptor descriptor,
            final CursorManager cursor, final AppDetails appDetails) {
        super();
        databaseDescriptor = descriptor;
        cursorManager = cursor;

        this.setLayout(new BorderLayout());
        
        // OUTPUT --------------------------------------------------------------
        final JTabbedPane outputTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
        this.add(outputTabbedPane, BorderLayout.CENTER);
        
        outputConsole = new TextAreaOutputConsole();
        outputTabbedPane.add(outputConsole.getTextArea(), "Console");

        final ResultTable resultTable = new ResultTable();
        outputTabbedPane.add(resultTable.getTable(), "Table");  // example!!

        final TableDisplay tableTableDisplay = new TableTableDisplay(resultTable);
        final TableDisplay consoleTableDisplay = new ConsoleTableDisplay(outputConsole);

        // INPUT ---------------------------------------------------------------
        inputConsole = new TextAreaInputConsole("<enter your SQL here>");
        inputConsole.addInputConsoleEventListener(new InputConsoleObserver());
        this.add(inputConsole.getTextArea(), BorderLayout.SOUTH);
        
        // Command Handlers ----------------------------------------------------
        final List<CommandHandler> commandHandlers = new ArrayList<CommandHandler>();
        // SQLCommandHandler must go last, so everything else gets a chance at
        // processing help commands before we let H2 handle the majority.
        commandHandlers.add(new HistoryCommandHandler(outputConsole, inputConsole));
        commandHandlers.add(new SQLCommandHandler(outputConsole, databaseDescriptor, consoleTableDisplay, tableTableDisplay));
        commandProcessor = new CommandProcessor(outputConsole, commandHandlers);
        
        outputConsole.info("This is a diagnostic facility for "
            + appDetails.getApplicationName()
            + " internals and for working on H2 databases via SQL.");
        for (final CommandHandler handler : commandHandlers) {
            for (final String text : handler.getIntroText()) {
                outputConsole.info(text);
            }
        }
    }

    
    /**
     * Process a line of input by passing it to CommandProcessor.
     * @param inputLine the line of input.
     */
    public void processInputLine(final String inputLine) {
        assert SwingUtilities.isEventDispatchThread();
        if (inputLine.length() > 0) {
            cursorManager.hourglass(this.getClass().getSimpleName());
            new SwingWorker() {
    
                @Override
                public Object construct() {
                    commandProcessor.processCommand(inputLine);
                    return null;
                }
                
                @Override
                public void finished() {
                    cursorManager.normal(SQLTabPanel.this.getClass().getSimpleName());
                }
            } .start();
        }
    }

    /**
     * Close all resources
     */
    public void finished() {
        outputConsole.finished();
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
            
            cursorManager.hourglass(this.getClass().getSimpleName());
            new SwingWorker() {
            
                @Override
                public Object construct() {
                    commandProcessor.processCommand(inputLine);
                    return null;
                }
                
                @Override
                public void finished() {
                    cursorManager.normal(SQLTabPanel.this.getClass().getSimpleName());
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
}

package org.devzendo.minimiser.gui.console.input;

/**
 * Encapsulation of a command string and its index into the history list.
 * @author matt
 *
 */
public final class HistoryObject {
    private final int commandIndex;
    private final String commandString;

    /**
     * Create a historic command
     * @param index the index in the command history
     * @param command the command string
     */
    public HistoryObject(final int index, final String command) {
        this.commandIndex = index;
        this.commandString = command;
    }

    /**
     * @return the index of this command into the command history
     */
    public int getCommandIndex() {
        return commandIndex;
    }

    /**
     * @return the command string
     */
    public String getCommandString() {
        return commandString;
    }
}

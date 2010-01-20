package org.devzendo.minimiser.gui.console.input;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The History manages the history list on behalf of consoles.
 * It stores history and allows queries and transformations.
 * Note: this isn't GNU readline/history <em>yet!</em>
 * 
 * @author matt
 *
 */
public final class History {
    private List<HistoryObject> historyList;
    private Matcher plingNumberMatcher;
    private Matcher plingWordMatcher;
    
    /**
     * Construct an empty History.
     */
    public History() {
        historyList = new ArrayList<HistoryObject>();
        plingNumberMatcher = Pattern.compile("!(\\d+)").matcher("");
        plingWordMatcher = Pattern.compile("!(\\S+)").matcher("");
    }
    
    /**
     * How many items are there in the history?
     * @return the number of items, zero initially.
     */
    public int size() {
        return historyList.size();
    }
    
    /**
     * Query all of the entries in the history list
     * @return a List of HistoryObjects, containing the entire history
     */
    public List<HistoryObject> getAll() {
        final List<HistoryObject> outList = new ArrayList<HistoryObject>(historyList.size());
        for (final HistoryObject history : historyList) {
            outList.add(history);
        }
        return outList;
    }
    
    /**
     * Query the last few entries in the history list
     * @param quantity the number of history entries to return from the end of
     * the history list
     * @return the number of entries requested, or fewer, if there are not that
     * many entries in the list
     */
    public List<HistoryObject> getLast(final int quantity) {
        final List<HistoryObject> outList = new ArrayList<HistoryObject>(quantity);
        if (historyList.size() != 0) {
            final int lastElement = historyList.size() - 1;
            for (int i = Math.max(0, lastElement - quantity + 1); i < historyList.size(); i++) {
                outList.add(historyList.get(i));
            }
        }
        return outList;
    }
    
    /**
     * Query for a specifically numbered entry in the history list
     * @param historyNumber the history number of the entry to return, note that
     * history entries start at 1!
     * @return the history entry, or null if the history number is out of range
     */
    public HistoryObject getNumberedEntry(final int historyNumber) {
        if (historyNumber < 1 || historyNumber > historyList.size()) {
            return null;
        }
        return historyList.get(historyNumber - 1);
    }
    
    /**
     * Add a command string to the end of the history list
     * @param command the command to add.
     */
    public void add(final String command) {
        historyList.add(new HistoryObject(historyList.size() + 1, command));
    }
    
    /**
     * If the input line contains a history transform operator (e.g. !23 )
     * then return the string containing that transformation, otherwise return
     * the original input.
     * <p>
     * The history transformations currently supported are:
     * <ul>
     * <li> <b>!<history number></b> - replace the operator with the contents
     *      of the given history entry
     * <li> <b>!start-of-earlier-history-entry</b> - replace the operator with
     *      with the contents of the last history entry that started with the
     *      given text
     * </ul>
     * <p>
     * @param inputLine the input line of text, which may contain history
     * transform operators
     * @return the original or transformed line
     * @throws HistoryTransformationException if the line contains a history
     * transform operator that cannot be transformed, e.g. if the history
     * list contains 5 elements and you request !20. Or, if !word does not
     * match any entry. 
     */
    public String transform(final String inputLine) throws HistoryTransformationException {
        final String plingNumberTransformed = transformPlingNumber(inputLine);
        final String plingWordTransformed = transformPlingEarlierWord(plingNumberTransformed);
        return plingWordTransformed;
    }

    private String transformPlingNumber(final String inputLine) throws HistoryTransformationException {
        plingNumberMatcher.reset(inputLine);
        final StringBuffer sb = new StringBuffer();
        while (plingNumberMatcher.find()) {
            final String historyReferenceString = plingNumberMatcher.group(1);
            try {
                final int historyNumber = Integer.parseInt(historyReferenceString);
                final HistoryObject historyEntry = getNumberedEntry(historyNumber);
                if (historyEntry == null) {
                    throw new HistoryTransformationException("History reference " + historyNumber + " does not exist");
                }
                plingNumberMatcher.appendReplacement(sb, historyEntry.getCommandString());
            } catch (final NumberFormatException nfe) {
                // should never happen!
                throw new HistoryTransformationException("History reference '"
                    + historyReferenceString 
                    + "' is non-numeric (shouldn't have been matched by regex)");
            }
        }
        plingNumberMatcher.appendTail(sb);
        return sb.toString();
    }
    
    private String transformPlingEarlierWord(final String inputLine) throws HistoryTransformationException {
        plingWordMatcher.reset(inputLine);
        final StringBuffer sb = new StringBuffer();
        while (plingWordMatcher.find()) {
            final String earlierWord = plingWordMatcher.group(1);
            System.out.println("earlierword '" + earlierWord + "'");
            boolean replaced = false;
            for (int h = historyList.size() - 1; h >= 0; h--) {
                final HistoryObject historyEntry = historyList.get(h);
                System.out.println("entry to compare: '" + historyEntry.getCommandString() + "'");
                if (historyEntry.getCommandString().startsWith(earlierWord)) {
                    plingWordMatcher.appendReplacement(sb, historyEntry.getCommandString());
                    replaced = true;
                    System.out.println("replaced");
                    break;
                }
            }
            if (!replaced) {
                throw new HistoryTransformationException("Reference to earlier history '!" + earlierWord + "' does not exist");
            }
        }
        plingWordMatcher.appendTail(sb);
        return sb.toString();
    }
}

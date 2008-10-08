package uk.me.gumbley.minimiser.gui.console.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class History {

    private List<HistoryObject> historyList;
    
    public History() {
        historyList = new ArrayList<HistoryObject>();
    }
    public int size() {
        return historyList.size();
    }
    public List<HistoryObject> getAll() {
        final List<HistoryObject> outList = new ArrayList<HistoryObject>(historyList.size());
        for (final HistoryObject history : historyList) {
            outList.add(history);
        }
        return outList;
    }
    public List<HistoryObject> getLast(final int quantity) {
        final List<HistoryObject> outList = new ArrayList<HistoryObject>(quantity);
        if (historyList.size() != 0) {
            final int lastElement = historyList.size() - 1;
            for (int i=Math.max(0, lastElement - quantity + 1); i < historyList.size(); i++) {
                outList.add(historyList.get(i));
            }
        }
        return outList;
    }
    public void add(final String command) {
        historyList.add(new HistoryObject(historyList.size() + 1, command));
    }
    public HistoryObject getIndex(final int index) {
        if (index < 1 || index > historyList.size()) {
            return null;
        }
        return historyList.get(index - 1);
    }

}

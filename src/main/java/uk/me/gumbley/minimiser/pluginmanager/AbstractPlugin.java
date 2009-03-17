package uk.me.gumbley.minimiser.pluginmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract base plugin type that you can extend to provide
 * default (i.e. empty) before/after plugin resolution lists.
 * 
 * @author matt
 *
 */
public abstract class AbstractPlugin implements Plugin {
    /**
     * Override this if you want to specify plugins to be
     * resolved after this one.
     * {@inheritDoc}
     */
    public List<String> getAfter() {
        return new ArrayList<String>();
    }

    /**
     * Override this if you want to specify plugins to be
     * resolved before this one.
     * {@inheritDoc}
     */
    public List<String> getBefore() {
        return new ArrayList<String>();
    }
}

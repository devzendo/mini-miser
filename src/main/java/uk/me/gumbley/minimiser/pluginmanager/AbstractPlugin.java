package uk.me.gumbley.minimiser.pluginmanager;

import java.util.ArrayList;
import java.util.List;

import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * An abstract base plugin type that you can extend to provide
 * default (i.e. empty) before/after plugin resolution lists.
 * Also handles storage of the provided SpringLoader.
 * 
 * @author matt
 *
 */
public abstract class AbstractPlugin implements Plugin {
    private SpringLoader mSpringLoader;
    
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
    
    /**
     * {@inheritDoc}
     */
    public final SpringLoader getSpringLoader() {
        return mSpringLoader;
    }

    /**
     * {@inheritDoc}
     */
    public final void setSpringLoader(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
    }
}

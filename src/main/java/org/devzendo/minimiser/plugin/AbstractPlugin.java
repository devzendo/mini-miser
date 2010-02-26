package org.devzendo.minimiser.plugin;

import org.devzendo.minimiser.springloader.SpringLoader;


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
     * {@inheritDoc}
     */
    public final SpringLoader getSpringLoader() {
        if (mSpringLoader == null) {
            throw new IllegalStateException("Cannot use the SpringLoader as it has not been set");
        }
        return mSpringLoader;
    }

    /**
     * {@inheritDoc}
     */
    public final void setSpringLoader(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
    }
}

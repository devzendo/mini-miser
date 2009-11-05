package uk.me.gumbley.minimiser.util;

/**
 * A Pair allows a pair of objects of specific types to be
 * returned from some method.
 *
 * @author matt
 *
 * @param <F> the type of the first object
 * @param <S> the type of the second object
 */
public final class Pair<F, S> {
    private final F mFirst;
    private final S mSecond;

    /**
     * @param first the first object
     * @param second the second object
     */
    public Pair(final F first, final S second) {
        mFirst = first;
        mSecond = second;
    }

    /**
     * @return the first
     */
    public F getFirst() {
        return mFirst;
    }

    /**
     * @return the second
     */
    public S getSecond() {
        return mSecond;
    }
}

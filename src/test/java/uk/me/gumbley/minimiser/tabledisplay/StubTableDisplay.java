package uk.me.gumbley.minimiser.tabledisplay;



/**
 * A simple table display that allows the AbstractTableDisplay base functions
 * to be tested.
 * 
 * @author matt
 *
 */
public final class StubTableDisplay extends AbstractTableDisplay {
    private int headingEmitCount;
    private int rowEmitCount;
    
    /**
     * Constructor 
     */
    public StubTableDisplay() {
        super();
        headingEmitCount = 0;
        rowEmitCount = 0;
    }
    
    /**
     * @return howmany headings have been emitted
     */
    public int getHeadingEmitCount() {
        return headingEmitCount;
    }
    
    /**
     * @return how many rows have been emitted
     */
    public int getRowEmitCount() {
        return rowEmitCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void emitHeading(final Row headings) {
        headingEmitCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void emitRow(final Row row) {
        rowEmitCount++;
    }

    /**
     * {@inheritDoc}
     */
    public void finish() {
        // nothing
    }
}

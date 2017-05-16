package calculators;

/**
 * Simple interface for calculators
 */
public interface Calculator {
    /** Calling this method will force the calculator to update the table*/
    public void recalculate(DistanceTable newTable);
}

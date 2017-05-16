package calculators;

/**
 * Calculates the uscore matrix.
 */
public class UScoreCalculator implements Calculator {

    /** the amount to subtract from the denominator when averaging for uscores*/
    private final static int DENOM_BIAS = 2;

    /** Our reference to the distances */
    private DistanceTable distances;

    @Override
    public void recalculate(DistanceTable newTable) {
        // no need to do any recalculation because this is the last step
        // (also avoids divide by 0 error)
        if (newTable.getClusterCount() <= 2) return;

        // loop through all entries and update the u-scores
        for (String cluster : newTable.getIDs()) {
            double uscore = newTable.getSumDistances(cluster);

            // now normalize
            uscore /= newTable.getClusterCount() - DENOM_BIAS;

            // now update the uscore
            newTable.setUScore(cluster, uscore);
        }
    }
}

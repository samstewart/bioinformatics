package calculators;

/**
 * Calculates the biased distances given a distance matrix and a scoring matrix.
 * The biased scores are derived scores and do not need to no anything about the state of the tree.
 */
public class BiasedDistanceCalculator implements Calculator {

    @Override
    public void recalculate(DistanceTable newTable) {
        // pretty straightforward calculation, for every entry, just subtract
        // uscores as slight bias from real distance

        // Note: we have some redundancy but it doesn't really matter (we are filling in entire
        // matrix instead of only one half)
        for (String cluster : newTable.getIDs()) {

            for (String other_cluster : newTable.getIDs()) {

                if ( ! cluster.equals(other_cluster) ) {
                    double biasedDistance = newTable.getDistance(cluster, other_cluster);
                    biasedDistance -= newTable.getUScore(cluster) + newTable.getUScore(other_cluster);

                    newTable.setBiasedDistance(cluster, other_cluster, biasedDistance);

                } else
                    // biased distance of MAX (never want to merge ourselves)
                    newTable.setBiasedDistance(cluster, other_cluster, 0);
            }
        }
    }
}

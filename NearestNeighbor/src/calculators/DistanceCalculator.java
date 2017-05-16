package calculators;

import tree.TreeNode;

/**
 * Calculates distances between clusters.
 */
public class DistanceCalculator implements Calculator {

    private String c1ToMerge;

    private String c2ToMerge;

    /**
     * we need this information to calculate the distances
     * because we need to know which two clusters we just merged.
     */
    public void setClustersToMerge(String cluster1, String cluster2) {
        this.c1ToMerge = cluster1;
        this.c2ToMerge = cluster2;
    }

    @Override
    public void recalculate(DistanceTable newTable) {
        if (c1ToMerge == null || c2ToMerge == null)
            return;

        // recompute all distances to the new cluster
        String mergedID = c1ToMerge + c2ToMerge;

        for (String cluster : newTable.getIDs()) {

            if (cluster.equals(mergedID))
                continue;

            // compute in terms of merged clusters
            double distance = 1.0 / 2.0 * (newTable.getDistance(c1ToMerge, cluster) + newTable.getDistance(c2ToMerge, cluster) - newTable.getDistance(c1ToMerge, c2ToMerge));

            // we have just calculated the new distance to the merged cluster we just built
            // between all the other elements.
            newTable.setDistance(cluster, mergedID, distance);
        }

        // reset to avoid re-running. These
        // need to be set every time we do a merge
        c1ToMerge = null;
        c2ToMerge = null;
    }

    /**
     * simple utility method to get the distance between a cluster
     * and the result of merging that cluster with another to form a new parent.
     */
    public double getDistanceAfterMerge(DistanceTable table, String c1, String c2) {
        // use the formula for finding distance from child to new merged parent.
        return 1.0 / 2.0 * (table.getDistance(c1, c2)  + table.getUScore(c1) - table.getUScore(c2));
    }
}

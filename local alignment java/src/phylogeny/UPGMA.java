package phylogeny;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple class that constructs a phylogentic tree using UPGMA algorithm
 */
public class UPGMA implements PhylogeneticTreeBuilder {

    private String findMinDistance(HashMap<String, Integer> scores) {
        // find the minimal distance
        Iterator it = scores.entrySet().iterator();

        String minKey = null;
        int minValue = Integer.MAX_VALUE;

        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)it.next();

            if (entry.getValue() < minValue) {
                minKey = entry.getKey();
                minValue = entry.getValue();
            }
        }

        return minKey;
    }

    /** converts a key to taxons*/
    private Taxon[] keyToTaxons() {
        return null;
    }
    @Override
    public Tree buildTree(HashMap<String, Integer> scores) {
        // look through all the scores and find the minimal score

        Tree tree = new Tree();

        // continue to consolidate the pairs
        while (scores.size() > 1) {
             String minKey = findMinDistance(scores);


        }

        return null;
    }
}

package phylogeny;

import sun.jvm.hotspot.utilities.HashtableBucket;

import java.util.HashMap;

/**
 * Simple tree builder.
 */
public interface PhylogeneticTreeBuilder {

    /** Builds a tree based off a scoring hash table where scores are entered as taxon1:taxon2*/
    public Tree buildTree(HashMap<String, Integer> scores);
}

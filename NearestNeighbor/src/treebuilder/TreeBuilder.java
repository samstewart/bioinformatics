package treebuilder;

import tree.Taxon;
import tree.Tree;

import java.util.List;

/**
 * Abstract interface all tree builders must implement.
 */
public interface TreeBuilder {
    /** Constructs a new phylogenetic tree based of a distance matrix and some taxa.
     * It then roots the tree using the specified outgroup
     */
    public Tree buildTree(int[][] distances, List<Taxon> taxa, Taxon outgroup);
}

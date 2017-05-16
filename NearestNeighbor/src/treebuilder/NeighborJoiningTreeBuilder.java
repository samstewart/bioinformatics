package treebuilder;

import calculators.DistanceTable;
import tree.Taxon;
import tree.Tree;
import tree.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Constructs a phylogenetic tree using the neighbor joining method.
 */
public class NeighborJoiningTreeBuilder implements TreeBuilder {

    /** table for calculating distances */
    protected DistanceTable table;

    /** our list of taxa*/
    private List<Taxon> taxa;

    /** The tree we are constructing. */
    private Tree tree;

    /** pool of every single cluster we need to maintain so that we can easily
     * access any node in the tree.
     */
    private HashMap<String, TreeNode> nodePool;

    @Override
    public Tree buildTree(int[][] distances, List<Taxon> taxa, Taxon outgroup) {
        this.taxa = taxa;

        this.nodePool = new HashMap<String, TreeNode>();

        // add all the leaves to the node pool
        ArrayList<String> ids = new ArrayList<String>();

        for (Taxon taxon : taxa) {
            ids.add(taxon.auto_name);

            // put the leave tree nodes in the pool
            this.nodePool.put(taxon.auto_name, new TreeNode(taxon));
        }

        table = new DistanceTable(distances, ids);

        while (table.getClusterCount() > 2) {
            // pick lowest two elements and merge
            // this method includes the tie breaking stuff
            String[] clusters = findClosestClusters();

            String c1 = clusters[0];
            String c2 = clusters[1];

            // now let's build this into the tree
            // we want to use the old uscores, so we don't update the table yet
            joinTreeNodes(c1, c2);

            // update the distances for the next step
            // it is crucial that we do this *after* we join the tree nodes
            table.mergeClusters(c1, c2);

        }

        // we need special case logic for handling the last two clusters
        // because we want to join them directly to each other
        String[] clusters = findClosestClusters();

        String c1 = clusters[0];
        String c2 = clusters[1];

        joinLastTreeNodes(c1, c2);

        // this needs to happen after we update the tree.
        table.mergeClusters(c1, c2);

        // use the outgroup to create a rooted tree
        this.tree = createRootedTree(outgroup.auto_name);

        return tree;
    }


    /**
     * splices in a node between two nodes then re-rootifies the entire tree using
     * this new node we just spliced in.
     * Since we only use leaves as out groups, we need the parent and the leaf.
     * @return the new root as a result of the splicing operation
     */
    public Tree createRootedTree(String cluster1) {
        TreeNode leaf = nodePool.get(cluster1);


        System.out.println(new Tree(leaf.parent));

        if (leaf == null || ! leaf.isLeaf())
            throw new IndexOutOfBoundsException("Outgroup must be a leaf node");

        // splice in a new parent between the leaf and it's parent
        // TODO: ordering matters here?
        return new Tree(leaf.rootify());

    }
    /**
     * Joins two nodes without actually creating a third node.
     * Used on last two clusters in the tree.
     */
    public void joinLastTreeNodes(String c1, String c2) {
        TreeNode c1Node = nodePool.get(c1);
        TreeNode c2Node = nodePool.get(c2);

        // get distances to each other
        c1Node.distanceToParent = table.getDistance(c1, c2);
        // we need to go both directions
        c2Node.distanceToParent = table.getDistance(c1, c2);

        // set each other as parents
        // we need this bidirection so that we can go "either" way over the "bridge"
        // that connects the two subtrees
        c1Node.parent = c2Node;
        c2Node.parent = c1Node;
    }
    /**
     * Joins two nodes together in the tree (finds distances, etc)
     * @return the new parent we just created
     */
    public void joinTreeNodes(String c1, String c2) {
        TreeNode c1Node = nodePool.get(c1);
        TreeNode c2Node = nodePool.get(c2);

        // whip up a new node
        // TODO: might be some ordering tension here
        TreeNode newParent = new TreeNode(c1Node, c2Node);

        c1Node.parent = newParent;
        c2Node.parent = newParent;

        // tell the new nodes their distance to the new cluster
        c1Node.distanceToParent = table.getDistanceAfterMerge(c1, c2);

        // we need to go both directions
        // tell other node about his distance to the new cluster
        c2Node.distanceToParent = table.getDistanceAfterMerge(c2, c1);

        // add new parent to the pool as a top level node
        nodePool.put(newParent.id, newParent);
    }

    /**
     * Finds the two clusters that are closest together using the biased distance score.
     * It breaks ties using the biasing order that Adam gave us.
     */
    protected String[] findClosestClusters() {
        String minC1 = null;
        String minC2 = null;

        double minDistance = Double.MAX_VALUE;
        double dist;

        for (String cluster : table.getIDs()) {

            // now compare to all the others and see which is smallest
            for (String other_cluster : table.getIDs()) {

                // ignore ourselves of course
                if ( ! cluster.equals(other_cluster)) {

                    dist = table.getBiasedDistance(cluster, other_cluster);

                    // TODO: we might later "undo" our broken tie if we are looking in the wrong ordering?
                    if (dist < minDistance) {
                        minDistance = dist;

                        minC1 = cluster;
                        minC2 = other_cluster;
                    } else if (dist == minDistance) { // need to break the tie
                        minDistance = dist;

                        // make sure we pick the proper cluster
                        String[] broken_tie = breakTie(minC1, minC2, cluster, other_cluster);
                        minC1 = broken_tie[0];
                        minC2 = broken_tie[1];
                    }

                }
            }
        }

        return new String[] {minC1, minC2};
    }

    /** Normalizes the ordering so that the earliest (alphabetically) item comes first*/
    protected String[] normalizeOrdering(String c1, String c2) {
        if (c1.compareTo(c2) < 0)
            return new String[] {c1, c2};
        else
            return new String[] {c2 , c1};
    }
    /**
     * Simple method to break tie between any two pairs of clusters that are going to be merged.
     * @return the correct pair to use
     */
    protected String[] breakTie(String c11, String c12, String c21, String c22) {
        // we want to normalize the ordering to ensure a proper comparison.
        // If we get B-A we want to flip it to A-B to ensure it gets a proper rank
        String[] ordering = normalizeOrdering(c11, c12);
        c11 = ordering[0];
        c12 = ordering[1];

        ordering = normalizeOrdering(c21, c22);
        c21 = ordering[0];
        c22 = ordering[1];

        // try the obvious "old vs new". The older clusters will be shorter than the newer clusters
        String joined0 = c11 + c12;
        String joined1 = c21 + c22;

        // do we want to be comparing their sum length?
        if (joined0.length() < joined1.length())
            return new String[] {c11, c12};

        else if (joined1.length() < joined0.length())
            return new String[] {c21, c22};

        // now do alphabetical tie breaking assuming they are the same length
        int comparison = joined0.compareTo(joined1);

        // TODO: joining them implies a certain ordering which may be problematic
        if (comparison < 0) {
            return new String[] {c11, c12};
        } else {
            return new String[] {c21, c22}; // never equality since different strings so
                                            // positive implies joined0 comes *after*
                                            // joined1
        }
    }
}

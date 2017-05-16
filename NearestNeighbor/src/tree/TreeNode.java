package tree;

import java.util.ArrayList;

/**
 * Represents a single node in the tree. We hard code a maximum degree of 3.
 * We do something tricky:
 * We start by building the tree by assuming a parent,left,right configuration
 * then we "rootify" the tree and adjust the connections to make printing easier.
 * It helps to think of it as rooted when we're building and then we just adjust
 * the roots once we've built it.
 * Tree nodes cannot print themselves without an outputter.
 */
public class TreeNode {

    /** connection to our second neighbor*/
    public TreeNode parent;

    /** connection to our left neighbor*/
    public TreeNode left_child;

    /** connection to our right neighbor*/
    public TreeNode right_child;

    /** distance to our parent */
    public double distanceToParent;

    /** Unique ID of this node (may look like a name but not accurate).*/
    public String id;

    /** The taxa associated with this node. It is null if this node is not a leaf node*/
    public Taxon taxon;

    public TreeNode(Taxon taxon) {
        this.taxon = taxon;
        this.id = this.taxon.auto_name;
    }

    /** Constructs a new tree node by joining together these two children*/
    public TreeNode(TreeNode left, TreeNode right) {
        this.right_child    = right;
        this.left_child     = left;
        left.parent         = this;
        right.parent        = this;

        this.taxon = null; // means we are an internal node
        this.id = this.left_child.id + this.right_child.id;
    }

    /** This node is a leaf is the taxon it has is not null*/
    public boolean isLeaf() {
        return (taxon != null);
    }

    /** returns whether or not we are a "root"*/
    public boolean isRoot() {
        return (parent == null);
    }
    /**
     * recursively determines this nodes name based on current tree's "orientation".
     * It sorts each child by its name
     */
    public String getTrueName() {
        if (isRoot())
            return "Root";

        if (isLeaf())
            return taxon.auto_name;

        String lName = "";
        String rName = "";

        // TODO: slightly inefficient to concatenate strings but whatever
        if (left_child != null)
            lName = left_child.getTrueName();

        if (right_child != null)
            rName = right_child.getTrueName();

        return (lName.compareTo(rName) < 0 ? lName + rName : rName + lName);
    }

    /** Splices on a new root node that joins the current node and the rest of the tree*/
    public TreeNode rootify() {
        if (! isLeaf())
            return null; // we must be a leaf in order to be a new root

        // we will use "ourselves" as root, tree needs to point to me
        TreeNode oldParent = rootify(null);

        // TODO: we should make ourselves left or right depending on whether
        // we are left or right child of parent
        // This needs to happen *after* re-rooting the tree
        TreeNode newRoot = new TreeNode(this, oldParent);

        // we need to halve the distance to this new parent
        // because we placed a node halfway in between the leaf
        // and the parent

        this.distanceToParent /= 2.0;
        oldParent.distanceToParent = this.distanceToParent; // should be same thing as us because "other half"

        return newRoot;
    }

    /** Reorients the entire tree to use the specified node as the root.
     * It returns the old parent of the root since we need this later*/
    public TreeNode rootify(TreeNode newParent) {
        TreeNode oldParent = this.parent;

        // TODO: need to properly update distances
        this.parent = newParent; // new parent (if newParent is null, that means we are the root)

        // if the new parent is null, that means we are the new root but we still need to adjust our children.
        TreeNode oldRight = right_child;
        TreeNode oldLeft  = left_child;

        // we have to rotate the entire set of connections
        // if either the left or right child is the new parent
        // you can think of this change as a kind of "rotation"
        if (newParent == right_child && ! isLeaf()) {
            // swap in old parent for right child if we are no coming in from the right
            if (right_child != null)
                this.distanceToParent = right_child.distanceToParent;
            else
                this.distanceToParent = -1.0; // we are a leaf node and now root

            this.left_child  = oldParent;
            this.right_child = oldLeft;
        } else if (newParent == left_child && !isLeaf()) {
            // swap in old parent for left child if we are now coming in from the left
            // swap distance as well because undirected
            if (left_child != null)
                this.distanceToParent = left_child.distanceToParent;
            else
                this.distanceToParent = -1.0; // we are a leaf node and now root

            this.right_child = oldParent;
            this.left_child  = oldRight;
        }

        // we only need to tell our parent about the change
        // since everything under us is properly rooted.
        // since we've rotated, our old parent has become one of our children
        // and we need to tell it that this has happened.
        // we need to avoid a cycle by checking to make sure that our
        // new parent is not also our old one
        // we need to check for null because this may be the root we are re-orienting.
        // If we don't perform this check we will try to "climb back up" and create a cycle.
        // This edge case oly seems to appear when crossing the "bridge" completed in the last step
        if (oldParent != newParent && oldParent != null) {
            oldParent.rootify(this);
        }

        return oldParent;
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("You should use an 'Outputter' to display the output");
    }
}

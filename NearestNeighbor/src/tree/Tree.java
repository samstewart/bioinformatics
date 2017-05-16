package tree;

import gui.outputters.AdamFormat;
import gui.outputters.Outputter;

/**
 * Represents a single tree.
 */
public class Tree {
    /** the root of the tree*/
    public TreeNode root;

    /** the output formatter. Default to Adam's format.*/
    private Outputter outputter = new AdamFormat();


    public void setOutputter(Outputter outputter) {
        this.outputter = outputter;
    }

    public Tree(TreeNode root) {
        this.root = root;
    }

    public Tree() {
        this.root = null;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public String toString() {
        // just recursively print out this tree
        return outputter.output(this);
    }

}

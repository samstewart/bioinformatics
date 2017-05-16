package gui.outputters;

import tree.Tree;
import tree.TreeNode;

/**
 * Interface for outputting the tree structure. we currently
 * imply a recursive algorithm.
 */
public abstract class Outputter {
    public String output(Tree tree) {
        StringBuilder builder = new StringBuilder();

        // treat ourselves as the root and print out the tree
        outputNode(tree.root, builder, 0);

        return builder.toString();
    }

    public String output(TreeNode rootNode) {
        StringBuilder builder = new StringBuilder();

        // treat ourselves as the root and print out the tree
        outputNode(rootNode, builder, 0);

        return builder.toString();
    }

    public abstract void outputNode(TreeNode node, StringBuilder builder, int level);
}

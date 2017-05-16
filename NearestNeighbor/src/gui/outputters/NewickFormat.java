package gui.outputters;

import tree.Tree;
import tree.TreeNode;

/**
 * Prints out in newick format for graphing with external tools.
 */
public class NewickFormat extends Outputter {

    private boolean showInternal = false;

    public void setShowInternalNodes(boolean showInternal) {
        this.showInternal = showInternal;
    }

    @Override
    public void outputNode(TreeNode node, StringBuilder builder, int level) {
        if (node.isLeaf()) {
            builder.append(node.taxon.name);
            return;
        }

        builder.append("(");

        // Note: parents should tack on distance to the children

        // we are an internal node
        // if we don't have both kids, skip us as a level
        if (node.left_child == null || node.right_child == null)
            level--;

        // only binary tree so two kids
        // ordering doesn't matter here (see the docs on tree drawers)
        if (node.left_child != null) {
            outputNode(node.left_child, builder, level + 1);
            builder.append(":" + node.left_child.distanceToParent);
        }

        if (node.right_child != null) {
            builder.append(",");
            outputNode(node.right_child, builder, level + 1);

            // append the distance
            builder.append(":" + node.right_child.distanceToParent);
        }

        builder.append(")");


        // if we want to show the internal node names
        if (showInternal)
            builder.append(node.getTrueName());


        if (node.isRoot()) {
            builder.append(";");  // note that we are a tree so we need final separator
        }
    }
}

package gui.outputters;

import tree.Tree;
import tree.TreeNode;

/**
 * Format for Adam's output.
 */
public class AdamFormat extends Outputter {


    @Override
    public void outputNode(TreeNode node, StringBuilder builder, int level) {
        String tabs = "";

        // TODO: add null protections
        // build up the number of tabs, depending on my level
        for (int i = 0; i < level; i++) {
            tabs += "\t"; // might want to use string builder? not sure it matters for shallow tree
        }

        if (node.isLeaf()) {
            builder.append(String.format("%sNode %s\t\t\t", tabs, node.getTrueName()));
            builder.append(String.format("(%s)\n", node.taxon.name));
            return;
        }

        String rTrueName = "";
        String lTrueName = "";
        double rDist = 0.0;
        double lDist = 0.0;



        if (node.right_child != null) {
            rTrueName = node.right_child.getTrueName();
            rDist = node.right_child.distanceToParent;
        }


        if (node.left_child != null) {
            lTrueName = node.left_child.getTrueName();
            lDist = node.left_child.distanceToParent;
        }

        // we sort to get a nice ordering because binary trees have no implicit ordering
        boolean leftFirst = (lTrueName.compareTo(rTrueName) < 0);

        // we have to have two non-null children
        // otherwise we just recurse into the children
        if (node.left_child != null && node.right_child != null) {
            // we don't call (get true name)  because it would be redundant (we need rTrueName and lTrueName later).
            // TODO: slightly inefficient to call this.getTrueName() and then call it again later for the left and right subtree

            builder.append(String.format("%sNode %s\t\t\t", tabs, node.getTrueName()));

            if (leftFirst)
                // we tell who we are connected to
                builder.append(String.format("connected to %s (%.2f), %s (%.2f)\n", lTrueName, lDist, rTrueName, rDist));
            else
                builder.append(String.format("connected to %s (%.2f), %s (%.2f)\n", rTrueName, rDist, lTrueName, lDist));

        } else {
            // decrease the level by one because we are skipping ourselves
            // and heading straight to the kids
            level--;
        }


        // the ordering matters because we are modifying the string builder
        if (leftFirst && node.left_child != null)  outputNode(node.left_child, builder, level + 1);
        if (leftFirst && node.right_child != null) outputNode(node.right_child, builder, level + 1);
        if (! leftFirst && node.right_child != null) outputNode(node.right_child, builder, level + 1);
        if (! leftFirst && node.left_child != null) outputNode(node.left_child, builder, level + 1);
    }
}

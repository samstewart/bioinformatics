package tree;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class TreeTest {

    @Test
    public void testShouldSetRoot() {
         TreeNode root = new TreeNode(new Taxon("Species 6", "B"));

         Tree tree = new Tree(root);

         assertEquals(root, tree.root);

         tree.setRoot(null);

         assertNull(tree.root);
    }

    @Test
    public void testShouldPrintNiceString() {
        Taxon taxon1 = new Taxon("Species 6", "A");
        Taxon taxon2 = new Taxon("Species 7", "B");

        TreeNode left = new TreeNode(taxon1);
        TreeNode right = new TreeNode(taxon2);

        left.distanceToParent = 1.3;
        right.distanceToParent = 3.4;

        TreeNode joined12 = new TreeNode(left, right);
        joined12.distanceToParent = 1.67;

        Taxon taxon3 = new Taxon("Species 8", "C");

        TreeNode node3 = new TreeNode(taxon3);
        node3.distanceToParent = 5.6;

        TreeNode joined123 = new TreeNode(joined12, node3);
        assertNull(joined123.parent);

        // TODO: ensure we have the proper string output
        assertEquals("Node Root\t\t\tconnected to AB (1.67), C (5.60)\n" +
                "\tNode AB\t\t\tconnected to A (1.30), B (3.40)\n" +
                "\t\tNode A\t\t\t(Species 6)\n" +
                "\t\tNode B\t\t\t(Species 7)\n" +
                "\tNode C\t\t\t(Species 8)\n", joined123.toString());
    }

}

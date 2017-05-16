package tree;

import gui.outputters.AdamFormat;
import gui.outputters.Outputter;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: samstewart
 * Date: 3/6/13
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeNodeTest {

    private Outputter outputter;

    @Before
    public void setup() {
        this.outputter = new AdamFormat();
    }

    @Test
    public void shouldBeALeafIfHasTaxon() {
        Taxon taxon = new Taxon("Species 6", "A");

        TreeNode node = new TreeNode(taxon);

        assertTrue(node.isLeaf());

    }

    @Test
    public void shouldNotBeALeafIfNoTaxon() {
        Taxon taxon1 = new Taxon("Species 6", "A");
        Taxon taxon2 = new Taxon("Species 7", "B");

        TreeNode left = new TreeNode(taxon1);
        TreeNode right = new TreeNode(taxon2);


        TreeNode node = new TreeNode(left, right);
        assertEquals("AB", node.id);
        assertFalse(node.isLeaf());

    }

    @Test
    public void shouldUpdateChildrenWhenInitialized() {
        Taxon taxon1 = new Taxon("Species 6", "A");
        Taxon taxon2 = new Taxon("Species 7", "B");

        TreeNode left = new TreeNode(taxon1);
        TreeNode right = new TreeNode(taxon2);


        TreeNode node = new TreeNode(left, right);

        assertSame(node, left.parent);
        assertSame(node, right.parent);

        assertSame(left, node.left_child);
        assertSame(right, node.right_child);
    }


    @Test
    public void shouldProperlyRootify() {
         // setup a mini testing tree
        Taxon taxon1 = new Taxon("Species 6", "A");
        Taxon taxon2 = new Taxon("Species 7", "B");

        TreeNode left = new TreeNode(taxon1);
        TreeNode right = new TreeNode(taxon2);


        TreeNode node = new TreeNode(left, right);

        fail("Test not implemented");
    }

    @Test
    public void shouldGetTrueNameBasedOnRealTree() {
        // setup a mini tree
        /**
         * Initial structure then we insert a new node above B and make it the root.
         *     |
         *   /  \
         *  |    |
         * / \    \
         * A  B    C
         */
        Taxon taxon1 = new Taxon("Species 6", "A");
        Taxon taxon2 = new Taxon("Species 7", "B");

        TreeNode left = new TreeNode(taxon1);
        TreeNode right = new TreeNode(taxon2);

        TreeNode joined12 = new TreeNode(left, right);

        Taxon taxon3 = new Taxon("Species 8", "C");

        TreeNode node3 = new TreeNode(taxon3);

        TreeNode joined123 = new TreeNode(joined12, node3);

        assertEquals("Root", joined123.getTrueName());

        assertSame(joined12, right.parent);


        TreeNode newRoot = right.rootify();
        assertNull(newRoot.parent); // should be a root
        assertEquals("Root", newRoot.getTrueName());

        System.out.println(outputter.output(newRoot));

        assertEquals(joined12, newRoot.right_child);
        assertEquals(right,    newRoot.left_child);
    }

    @Test
    public void testShouldProperlyReRootDistances() {
        fail("Test not implemented");
    }

    @Test
    public void shouldUseTaxonAutoNameAsID() {
        Taxon taxon = new Taxon("Species 6", "A");

        TreeNode node = new TreeNode(taxon);

        assertEquals("A", node.id);
        assertSame(taxon, node.taxon);
        assertTrue(node.isLeaf());
    }

    @Test
    public void shouldPrintOutInAlphabeticalOrder() {
        fail("Not implemented");
    }

    @Test
    public void shouldOutputRootInsteadOfCombinedChildren() {
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
        assertEquals("Root", joined123.getTrueName());
    }
    @Test
    public void shouldOutputNicelyFormattedStringRepresentation() {
        // setup a mini tree
        /**
         * Initial structure then we insert a new node above B and make it the root.
         *     |
         *   /  \
         *  |    |
         * / \    \
         * A  B    C
         */
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

        Tree tree = new Tree(joined123);

        // TODO: ensure we have the proper string output
        assertEquals("Node Root\t\t\tconnected to AB (1.67), C (5.60)\n" +
                "\tNode AB\t\t\tconnected to A (1.30), B (3.40)\n" +
                "\t\tNode A\t\t\t(Species 6)\n" +
                "\t\tNode B\t\t\t(Species 7)\n" +
                "\tNode C\t\t\t(Species 8)\n", tree.toString());
    }
}

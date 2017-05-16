package gui.outputters;

import calculators.DistanceTableTest;
import org.junit.Test;
import tree.Taxon;
import tree.Tree;
import treebuilder.NeighborJoiningTreeBuilder;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class NewickFormatTest {

    @Test
    public void shouldProperlyConstructSmallTree() {
        NeighborJoiningTreeBuilder builder = new NeighborJoiningTreeBuilder();

        Taxon taxon1 = new Taxon("Species 1", "A");
        Taxon taxon2 = new Taxon("Species 2", "B");
        Taxon taxon3 = new Taxon("Species 3", "C");
        Taxon taxon4 = new Taxon("Species 4", "D");
        Taxon taxon5 = new Taxon("Species 5", "E");
        Taxon taxon6 = new Taxon("Species 6", "F");

        ArrayList<Taxon> taxa = new ArrayList<Taxon>();

        taxa.add(taxon1);
        taxa.add(taxon2);
        taxa.add(taxon3);
        taxa.add(taxon4);
        taxa.add(taxon5);
        taxa.add(taxon6);

        Tree tree = builder.buildTree(DistanceTableTest.getSmallTestMatrix1(), taxa, taxon6);


        // the root should actually join taxon 6 with the rest of the tree
        // we want the last group to be the outgroup
        assertNotSame(tree.root, taxon6);
        assertTrue(taxon6 == tree.root.left_child.taxon || taxon6 == tree.root.right_child.taxon);

        NewickFormat outputter = new NewickFormat();

        tree.setOutputter(outputter);
        System.out.println(tree.toString());

    }

    @Test
    public void testShouldIncludeInternalNodes() {
        fail("Not implemented");
    }
}

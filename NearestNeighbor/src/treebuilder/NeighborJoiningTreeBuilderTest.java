package treebuilder;

import calculators.DistanceTable;
import calculators.DistanceTableTest;
import org.junit.Test;
import tree.Taxon;
import tree.Tree;

import java.util.ArrayList;

import static junit.framework.Assert.*;

public class NeighborJoiningTreeBuilderTest {

    @Test
    public void shouldProperlyJoinLeastDistances() {
         fail("Not implemented");
    }

    @Test
    public void shouldProperlyBreakTies() {
        NeighborJoiningTreeBuilder builder = new NeighborJoiningTreeBuilder();

        assertEquals("A", builder.breakTie("A", "B", "C", "D")[0]);
        assertEquals("B", builder.breakTie("A", "B", "C", "D")[1]);

        assertEquals("C", builder.breakTie("C", "D", "AB", "E")[0]);
        assertEquals("D", builder.breakTie("C", "D", "AB", "E")[1]);

        // TODO: test break ties in the other direction

        assertEquals("C", builder.breakTie("D", "C", "E", "AB")[0]);
        assertEquals("D", builder.breakTie("C", "D", "AB", "E")[1]);
    }

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

        System.out.println(tree.toString());

        assertEquals("Node Root\t\t\tconnected to ABCDE (2.50), F (2.50)\n" +
                "\tNode ABCDE\t\t\tconnected to ABC (1.00), DE (1.00)\n" +
                "\t\tNode ABC\t\t\tconnected to AB (1.00), C (2.00)\n" +
                "\t\t\tNode AB\t\t\tconnected to A (1.00), B (4.00)\n" +
                "\t\t\t\tNode A\t\t\t(Species 1)\n" +
                "\t\t\t\tNode B\t\t\t(Species 2)\n" +
                "\t\t\tNode C\t\t\t(Species 3)\n" +
                "\t\tNode DE\t\t\tconnected to D (3.00), E (2.00)\n" +
                "\t\t\tNode D\t\t\t(Species 4)\n" +
                "\t\t\tNode E\t\t\t(Species 5)\n" +
                "\tNode F\t\t\t(Species 6)\n", tree.toString());

    }
}

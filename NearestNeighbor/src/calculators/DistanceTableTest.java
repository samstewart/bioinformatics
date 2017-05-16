package calculators;


import gui.Util;
import junit.extensions.TestSetup;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class DistanceTableTest {

    /** some useful test data grabbed from his website*/
    private int[][] testInitialDistances;

    private static final int[][] convertToLowerTriangular(int[][] upper_triangular) {
        // flip the initial distances to lower triangular since that's
        // what the distance table expects.
        // a bit hacky, but it's fine.
        // we simply do a matrix tranpose
        int[][] lowerTrianTestInitialDistances = new int[6][];

        // initialize the lower triangular
        for (int i = 0; i < 6; i++) {
            lowerTrianTestInitialDistances[i] = new int[i + 1];
        }

        // now swap rows and column
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < upper_triangular[i].length; j++) {
                // we have to add j + i to get proper offset since the ragged array above still starts at zero
                // (does not start at i for column)
                lowerTrianTestInitialDistances[j + i][i] = upper_triangular[i][j];
            }
        }

        return lowerTrianTestInitialDistances;
    }
    /** Creates a small testing matrix (pretty hacky)*/
    public static final int[][] getSmallTestMatrix1() {
        int[][] data =  {
            {0, 5, 4, 7, 6, 8},
            {0, 7, 10, 9, 11},
            {0, 7, 6, 8},
            {0, 5, 9},
            {0, 8},
            {0}};
        return convertToLowerTriangular(data);
    }

    /** creates another small testing matrix*/
    public static final int[][] getSmallTestMatrix2() {
        return null;
    }
    /** some fake test IDs*/
    private List<String> fakeIDs;

    @Before
    public void setup() {

        this.testInitialDistances = getSmallTestMatrix1();

        fakeIDs = new ArrayList<String>();

        fakeIDs.add("A");
        fakeIDs.add("B");
        fakeIDs.add("C");
        fakeIDs.add("D");
        fakeIDs.add("E");
        fakeIDs.add("F");
    }

    @Test
    public void testShouldProperlyInitializeTables() {
         DistanceTable table = new DistanceTable(this.testInitialDistances, this.fakeIDs);

        // make sure the IDs index properly
        assertEquals(0, table.clusterNamesToIndices.get("A").intValue());
        assertEquals(5, table.clusterNamesToIndices.get("F").intValue());


        assertEquals(2 * this.fakeIDs.size() - 1, table.distances.length);
        assertEquals(6, table.getClusterCount());
    }

    @Test
    public void testShouldProperlyComputeInitialUScores() {
        DistanceTable table = new DistanceTable(this.testInitialDistances, this.fakeIDs);


        assertEquals(7.5, table.getUScore("A"));
        assertEquals(10.5, table.getUScore("B"));
        assertEquals(8.0, table.getUScore("C"));
        assertEquals(9.5, table.getUScore("D"));
        assertEquals(8.5, table.getUScore("E"));
        assertEquals(11.0, table.getUScore("F"));

    }

    @Test
    public void testShouldComputeInitialDistanceScores() {
        DistanceTable table = new DistanceTable(this.testInitialDistances, this.fakeIDs);

        assertEquals(-13.0, table.getBiasedDistance("A", "B"));
        assertEquals(-13.0, table.getBiasedDistance("D", "E"));
    }

    @Test
    public void testShouldMergeClustersProperly() {
        DistanceTable table = new DistanceTable(this.testInitialDistances, this.fakeIDs);

        table.mergeClusters("A", "B");

        assertEquals(5, table.getClusterCount());

        // test distances to new cluster
        assertEquals(0.0, table.getDistance("AB", "AB"));
        assertEquals(3.0, table.getDistance("AB", "C"));
        assertEquals(6.0, table.getDistance("AB", "D"));
        assertEquals(5.0, table.getDistance("AB", "E"));
        assertEquals(7.0, table.getDistance("AB", "F"));

        // test new u-scores
        assertEquals(7.0, table.getUScore("AB"));
        assertEquals(8.0, table.getUScore("C"));
        assertEquals(9.0, table.getUScore("D"));
        assertEquals(8.0, table.getUScore("E"));
        assertEquals(10.666, table.getUScore("F"), .01);

        // try two of the biased distances
        assertEquals(-12.0, table.getBiasedDistance("C", "AB"));
        assertEquals(-12.0, table.getBiasedDistance("D", "E"));
    }
}

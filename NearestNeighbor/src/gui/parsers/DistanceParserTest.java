package gui.parsers;

import org.junit.Before;
import org.junit.Test;
import tree.Taxon;

import java.io.File;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class DistanceParserTest {

    private File test_file;

    private DistanceParser parser;

    @Before
    public void setup() {
        test_file = new File(this.getClass().getResource("data.txt").getFile());

        parser = new DistanceParser(test_file);
    }

    @Test
    public void testShouldAutoGenerateTaxaNames() {
        parser.parse(true);

        List<Taxon> taxa = parser.getTaxa();

        // try a few....maybe more?
        assertEquals("A", taxa.get(0).auto_name);

        assertEquals("D", taxa.get(3).auto_name);
    }
    @Test
    public void testShouldParseTaxaCorrectly() {
        parser.parse(true);

        List<Taxon> taxa = parser.getTaxa();

        assertEquals(15, taxa.size());

        // test a few elements
        assertEquals("NJ00-mosquito (AF404754)", taxa.get(2).name);

        assertEquals("Ug37 (M12294)", taxa.get(14).name);
    }

    @Test
    public void testShouldParseLowerTrianuglarDistancesCorrectly() {

        parser.parse(true); // lower triangular

        int[][] distances = parser.getDistances();

        // sample a few values
        assertEquals(232, distances[14][13]);
        assertEquals(2, distances[1][0]);
        assertEquals(11, distances[8][1]);
    }

    @Test
    public void testShouldParseUpperTriangularDistancesCorrectly() {
        fail("Not implemented");
    }
}

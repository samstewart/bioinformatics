import Alignment.SemiHalfGlobalAligner;
import Model.*;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Tests the semi-half global aligner.
 */
public class SemiHalfGlobalAlignerTest {

    SemiHalfGlobalAligner aligner;

    Sequence gene;

    @Before
    public void setup() {
        gene = new Sequence("AAGGT");

        aligner = new SemiHalfGlobalAligner(false); // go low road bias
    }
    @Test
    public void testShouldAlignSeq1() {
         Sequence chromosome = new Sequence("CCCCCCAAGGTCCCCCCC");

         Alignment alignment = aligner.findAlignment(chromosome, gene);

        assertEquals("best alignment is from [1,7] to [5,11]\n" +
                    "score is 5\n" +
                    "AAGGT\n" +
                    "AAGGT", alignment.toString(false)); // but the gene on the top when converting to a string

    }

    @Test
    public void testShouldAlignSeq2() {

        Sequence chromosome = new Sequence("AAAGCTAT");

        Alignment alignment = aligner.findAlignment(chromosome, gene);

        assertEquals("best alignment is from [1,2] to [5,6]\n" +
                "score is 3\n" +
                "AAGGT\n" +
                "AAGCT", alignment.toString(false)); // but the gene on the top when converting to a string
    }

    @Test
    public void testShouldTrimOffEndOfChromosome() {
        Sequence chromosome = new Sequence("AT");

        Sequence gene = new Sequence("A");

        Alignment alignment = aligner.findAlignment(chromosome, gene);

        assertEquals("best alignment is from [1,1] to [1,1]\n" +
                "score is 1\n" +
                "A\n" +
                "A", alignment.toString(false)); // but the gene on the top when converting to a string
    }

    @Test
    public void testShouldAlignSeq3() {
        Sequence chromosome = new Sequence("ACACAGGTAA");

        Alignment alignment = aligner.findAlignment(chromosome, gene);

        assertEquals("best alignment is from [1,4] to [5,8]\n" +
                "score is 3\n" +
                "AAGGT\n" +
                "CAGGT", alignment.toString(false)); // but the gene on the top when converting to a string
    }

    @Test
    public void testShouldAlignSeq4() {
        Sequence chromosome = new Sequence("CCAAGTCC");

        Alignment alignment = aligner.findAlignment(chromosome, gene);

        assertEquals("best alignment is from [1,3] to [5,6]\n" +
                "score is 2\n" +
                "AAGGT\n" +
                "AAG-T", alignment.toString(false)); // but the gene on the top when converting to a string
    }
}

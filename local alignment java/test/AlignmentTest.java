import Model.Sequence;
import junit.extensions.TestSetup;
import org.junit.Before;
import Model.Alignment;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Simple test for alignment.
 */
public class AlignmentTest {

    private Alignment alignment;

    private Sequence seq1;

    private Sequence seq2;

    @Before
    public void setup() {
        seq1 = new Sequence("AAGGT");

        seq2 = new Sequence("AAAGCTAT");

        alignment = new Alignment(seq1, seq2);

        alignment.setStart(seq1, 0);
        alignment.setEnd(seq1, seq1.length() - 1);

        alignment.setStart(seq1, 0);
        alignment.setEnd(seq2, seq2.length() - 1);
    }



    @Test
    public void testShouldSetScore() {
        alignment.setScore(5);

        assertTrue(alignment.toString(false).contains("score is 5"));
    }

}

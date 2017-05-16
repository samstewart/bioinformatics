package Model;

import java.util.ArrayList;

/**
 * Represents an alignment between two sequences. In the future we might wish to generalize this.
 */
public class Alignment {

    /** We need to build up the alignment sequences from the algorithm
     * so we use a string builder for maximum efficiency.
     */
    private StringBuilder gappedSeq1;

    /** We need to build up the alignment sequences from the algorithm
     * so we use a string builder for maximum efficiency.
     */
    private StringBuilder gappedSeq2;

    /**
     * The actual sequence (without gaps)
     */
    private Sequence seq1;

    /**
     * The actual sequence (without gaps)
     */
    private Sequence seq2;

    /** the start and end index of sequence 1*/
    private int[] seq1Indicies;

    /** the start and end index of sequence 2*/
    private int[] seq2Indicies;

    private int score;

    public Alignment(Sequence seq1, Sequence seq2) {

        this.seq1 = seq1;
        this.seq2 = seq2;

        seq1Indicies = new int[2];
        seq1Indicies[0] = 0;
        seq1Indicies[1] = seq1.length() - 1;

        seq2Indicies = new int[2];
        seq2Indicies[0] = 0;
        seq2Indicies[1] = seq2.length() - 1;

        gappedSeq1 = new StringBuilder(seq1.length());
        gappedSeq2 = new StringBuilder(seq2.length());

    }

    /** Converts this alignment to a string.
     *
     * @param seq1First determines if the first sequence is printed first.
     * @return a string describing this alignment.
     */
    public String toString(boolean seq1First) {



        if (seq1First) {
            return String.format("best alignment is from [%d,%d] to [%d,%d]\nscore is %d\n%s\n%s",
                    seq1Indicies[0] + 1, seq2Indicies[0] + 1,
                    seq1Indicies[1] + 1, seq2Indicies[1] + 1,
                    score,
                    gappedSeq1.toString(),
                    gappedSeq2.toString());
        } else {
            return String.format("best alignment is from [%d,%d] to [%d,%d]\nscore is %d\n%s\n%s",
                    seq2Indicies[0] + 1, seq1Indicies[0] + 1,
                    seq2Indicies[1] + 1, seq1Indicies[1] + 1,
                    score,
                    gappedSeq2.toString(),
                    gappedSeq1.toString());
        }



    }

    public void setStart(Sequence seq, int start) {
        if (seq1 == seq) {
            seq1Indicies[0] = start;
        } else if(seq2 == seq) {
            seq2Indicies[0] = start;
        }
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setEnd(Sequence seq, int end) {
        if (seq1 == seq) {
            seq1Indicies[1] = end;
        } else if(seq2 == seq) {
            seq2Indicies[1] = end;
        }
    }

    /** adds a character to one of the sequences we are building*/
    public void addCharacter(Sequence seq, char character) {
        // might be a bit inefficient to insert at the beginning each time.
        // what if we did an append then reversed when it was time to print?
        if (seq1 == seq) {
            gappedSeq1.insert(0, character);
        } else if(seq2 == seq) {
            gappedSeq2.insert(0, character);
        }
    }

    /**
     * adds a gap to the specified sequence
     */
    public void addGap(Sequence seq) {
        // might be a bit inefficient to insert at the beginning each time.
        // what if we did an append then reversed when it was time to print?
        if (seq1 == seq) {
            gappedSeq1.insert(0, LinearScorer.GAP);
        } else if(seq2 == seq) {
            gappedSeq2.insert(0, LinearScorer.GAP);
        }
    }
}

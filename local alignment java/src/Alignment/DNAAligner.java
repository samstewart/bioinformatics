package Alignment;

import Model.*;

/**
 * Aligner between two sequences.
 * I add this level of detail in case I want to add something special later.
 */
public interface DNAAligner extends Aligner {

    /** builds the alignment from the DP table of tracebacks and scores */
    public Alignment buildAlignmentFromTraceback(Sequence seq1, Sequence seq2, char[][] tracebacks, int[][] scores);

    /** initializes the tracebacks and scores array */
    public void initScoresAndTracebacks(int rows, int columns, char[][] tracebacks, int[][] scores);
}

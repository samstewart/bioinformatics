package Alignment;

import GUI.Util;
import Model.*;

/**

 * Uses the affine gap penalty for alignment.
*/
public class AffineGapSemiHalfGlobalAligner implements Aligner {

    /**
     * we don't want to go all the way down to the bounds of an int
     * as we want to allow some "padding" for operations.
     */
    private final int INFINITY = Integer.MIN_VALUE / 2;

    protected Scorer scorer;

    /** Determines how we break ties when looking at the scoring matrix*/
    private boolean biasHighroad;

    public AffineGapSemiHalfGlobalAligner(boolean doHighroad) {
        this.biasHighroad = doHighroad;
        this.scorer = new AffineScorer(-1, 1, -1, -1);
    }


    private void initScoresAndTracebacks(int rows, int columns, int[][] matchScores, int[][] seq1GapScores, int[][] seq2GapScores) {
        // initialize the three matrices as appropriate
        matchScores[0][0] = 0;

        AffineScorer scorer = (AffineScorer)this.scorer;

        for (int i = 0; i < rows; i++) {

            // we want the first column of the seq1 gaps to be penalized
            seq1GapScores[i][0] = scorer.gapStartPenalty + i * scorer.gapContinuePenalty;

            // across first row we need penalties for gaps in seq2
            for (int j = 0; j < columns; j++) {

                // put zeros in direct match scores everywhere else
                if (j != 0 || i != 0)
                    matchScores[i][j] = INFINITY;

                // all other entries of gap scores should be zero
                if (j > 0)
                    seq1GapScores[i][j] = INFINITY;

                if (i == 0)
                    // we should have the appropriate scores across the first column
                    seq2GapScores[i][j] = scorer.gapStartPenalty + j * scorer.gapContinuePenalty;
                else if (i > 0)
                    // all other entries should be infinity
                    seq2GapScores[i][j] = INFINITY;
            }
        }
    }

    @Override
    public Alignment findAlignment(Sequence seq1, Sequence seq2) {
        // we construct a new table for the DP algorithm

        // the first sequence (seq1) is on the y axis (rows) and the second (seq2) is on the x-axis (columns)
        // we need to add the extra zero columns on either side
        int rows 	= seq1.length() + 1;
        int columns = seq2.length() + 1;

        // the scores for exactly matching two characters
        int[][] matchScores = new int[rows][columns];

        // we construct tables for finding the traceback. Each entry is a character (only 8 bits)
        // that tells us where to go at each step.
        // We need three tables for three "levels" depending on how we are aligning gaps.
        // '<' - go to the left, we have a gap in the gene on the vertical axis
        // '^' - go up, we have a gap in the gene on the horizontal axis
        // '`' - go diagonal, we have on gap and consume both characters
        // 'X' - no information
        // we don't need the extra row and column
        // but we leave it in for simplicity anyway
        char[][] matchTracebacks = new char[rows][columns];

        char[][] seq1GapTracebacks = new char[rows][columns] ;

        char[][] seq2GapTracebacks = new char[rows][columns];

        // the table of scores for aligning characters in sequence 1 to gaps
        // this means the gaps that are in seq2
        int[][] seq1GapScores = new int[rows][columns];

        // the table of scores for aligning characters in sequence 2 to gaps
        // this means the gaps that are in seq1
        int[][] seq2GapScores = new int[rows][columns];

        // TODO: what happens if we don't specify one of the dimensions of the array in java
        initScoresAndTracebacks(rows, columns, matchScores, seq1GapScores, seq2GapScores);

        // an array for keeping track of 'align with character' scores of each direction
        int[] match_dir_scores = new int[3];

        // an array for tracking the 'align with gap' scores of each direction
        int[] gap_dir_scores  = new int[2];

        // now we fill in the DP table
        // start at one row below the top and one column to the right
        for (int row = 1; row < rows; row++) {

            // we have to account for the extra row
            int seq1Index = row - 1;

            for (int column = 1; column < columns; column++) {

                // null entry (going nowhere)
                matchTracebacks[row][column] = 'X';
                seq1GapScores[row][column] = 'X';
                seq2GapScores[row][column] = 'X';

                // we have to account for the extra column
                int seq2Index = column - 1;

                // find the best choice and enter it in the trace back

                // we add in the appropriate biases to break ties

                // we stick these scores in an array and loop through them. The array gives
                // the appropriate ordering for breaking ties using the low road/high road scoring

                // the current ordering is low road scoring; you'd need to reverse it to use highroad

                // exact match
                match_dir_scores[0] = matchScores[row - 1][column - 1] + ((AffineScorer)scorer).score(seq1.baseAt(seq1Index), seq2.baseAt(seq2Index));

                // we are building off a sub-sequence that had a gap in sequence 2
                match_dir_scores[1] = seq1GapScores[row - 1][column - 1] + ((AffineScorer)scorer).score(seq1.baseAt(seq1Index), seq2.baseAt(seq2Index));

                // we are building off a sub-sequence that had a gap in sequence 1
                match_dir_scores[2] = seq2GapScores[row - 1][column - 1] + ((AffineScorer)scorer).score(seq1.baseAt(seq1Index), seq2.baseAt(seq2Index));


                int maxScore = Integer.MIN_VALUE;
                int maxIndex = 0;

                // decide which order to go through
                for (int i = 0; i < match_dir_scores.length; i++) {

                    // decide which direction we are going using bias highroad or not
                    // if we are going highroad, we need to flip directions.
                    // if we aren't, we keep the same direction.
                    int scoreIndex = (biasHighroad ? (match_dir_scores.length - 1) - i : i);

                    // must be strictly greater than, otherwise we break ties with order
                    if (match_dir_scores[scoreIndex] > maxScore) {
                        maxIndex = scoreIndex;
                        maxScore = match_dir_scores[scoreIndex];
                    }
                }

                switch (maxIndex) {
                    case 0:
                        // stay in the current match matrix and consume two characters
                        matchTracebacks[row][column] = 'S';

                        matchScores[row][column] = maxScore;

                        break;

                    case 1:
                        // jump to the sequence 1 matrix and consume two characters
                        matchTracebacks[row][column] = '1';

                        matchScores[row][column] = maxScore;

                        break;

                    case 2:
                        // jump to the sequence 2 matrix and consume two characters
                        matchTracebacks[row][column] = '2';

                        matchScores[row][column] = maxScore;

                        break;

                }


                // now we look at the scores for the gaps aligned with sequence 1
                // we tell the scorer function that we are continuing a gap
                gap_dir_scores[0] = seq1GapScores[row - 1][column] + ((AffineScorer) scorer).gapContinuePenalty;

                gap_dir_scores[1] = matchScores[row - 1][column]  + ((AffineScorer) scorer).gapStartPenalty + ((AffineScorer) scorer).gapContinuePenalty;


                for (int i = 0; i < gap_dir_scores.length; i++) {
                    int scoreIndex = (biasHighroad ? (gap_dir_scores.length - 1) - i : i);

                    if (match_dir_scores[scoreIndex] > maxScore) {
                        maxIndex = scoreIndex;
                        maxScore = gap_dir_scores[scoreIndex];
                    }
                }

                switch (maxIndex) {
                    case 0:
                        // continue the gap
                        seq1GapTracebacks[row][column] = 'C';
                        seq1GapScores[row][column] = maxScore;
                        break;
                    case 1:
                        // start a new gap
                        seq1GapTracebacks[row][column] = 'N';
                        seq1GapScores[row][column] = maxScore;
                        break;

                }

                // now we look at the scores for the gaps aligned with sequence 2
                // we tell the scorer function that we are continuing a gap
                gap_dir_scores[0] = seq2GapScores[row][column - 1] + ((AffineScorer) scorer).gapContinuePenalty;

                gap_dir_scores[1] = matchScores[row][column - 1]  + ((AffineScorer) scorer).gapStartPenalty + ((AffineScorer) scorer).gapContinuePenalty;


                for (int i = 0; i < gap_dir_scores.length; i++) {
                    int scoreIndex = (biasHighroad ? (gap_dir_scores.length - 1) - i : i);

                    if (match_dir_scores[scoreIndex] > maxScore) {
                        maxIndex = scoreIndex;
                        maxScore = gap_dir_scores[scoreIndex];
                    }
                }

                switch (maxIndex) {
                    case 0:
                        // continue the gap
                        seq2GapTracebacks[row][column] = 'C';
                        seq2GapScores[row][column] = maxScore;
                        break;
                    case 1:
                        // start a new gap
                        seq2GapTracebacks[row][column] = 'N';
                        seq2GapScores[row][column] = maxScore;
                        break;

                }
            }
        }


        /*Util.print2DIntArray(matchScores);
        Util.print2DIntArray(seq1GapScores);
        Util.print2DIntArray(seq2GapScores);*/

        Util.print2DCharArray(matchTracebacks);
        Util.print2DCharArray(seq1GapTracebacks);
        Util.print2DCharArray(seq2GapTracebacks);

        // build final alignment out of all three of these matrices
        return buildAlignmentFromTraceback(seq1, seq2,
                                            matchTracebacks,
                                            seq1GapTracebacks,
                                            seq2GapTracebacks,
                                            seq1GapScores,
                                            seq2GapScores,
                                            matchScores);

    }

    @Override
    public Scorer getScorer() {
        return scorer;
    }


    public Alignment buildAlignmentFromTraceback(Sequence seq1, Sequence seq2,
                                                 char[][] matchTracebacks,
                                                 char[][] seq1GapTracebacks,
                                                 char[][] seq2GapTracebacks,
                                                 int[][] seq1GapScores,
                                                 int[][] seq2GapScores,
                                                 int[][] matchScores) {

        // we have to search through only the last column for the highest score
        int rows 	= seq1.length() + 1;
        int columns = seq2.length() + 1;


        // make a new alignment that we are building
        Alignment alignment = new Alignment(seq1, seq2);


        // decide which matrix to start in
        // by finding the max
        int[] endingMatrixScores = new int[3];

        // the ordering of these produces a bias in the case of ties
        endingMatrixScores[0] = matchScores[rows - 1][columns - 1];
        endingMatrixScores[1] = seq1GapScores[rows - 1][columns - 1];
        endingMatrixScores[2] = seq2GapScores[rows - 1][columns - 1];

        int maxScore = Integer.MIN_VALUE;
        short curMatrix = 0;

        for (int i = 0; i < endingMatrixScores.length; i++) {
            if (endingMatrixScores[i] > maxScore) {
                maxScore = endingMatrixScores[i];
                curMatrix = (short)i;
            }
        }

        // we also know this beast's score
        alignment.setScore(maxScore);

        int curRow    = rows - 1;
        int curColumn = columns - 1;

        // we need to account for extra column so we subtract off 1
        int seq1Index = curRow    - 1;
        int seq2Index = curColumn - 1;

        // we know where the alignment will end at least
        // we are working backwards
        alignment.setEnd(seq1, seq1Index);
        alignment.setEnd(seq2, seq2Index);



        // start at the far right column and work our way left
        while (curColumn != 0 || curRow != 0) {

            char direction = 'U';
            // decide where we are
            if (curMatrix == 0) {
                direction = matchTracebacks[curRow][curColumn];
            } else if (curMatrix == 1) {
                direction = seq1GapTracebacks[curRow][curColumn];
            } else if (curMatrix == 2) {
                direction = seq2GapTracebacks[curRow][curColumn];
            }

            System.out.println("Direction " + direction + "at (" + curRow + ", " + curColumn + ")");


            seq1Index = curRow    - 1;
            seq2Index = curColumn - 1;

            // adjust our starting index. this will eventually
            // be the first left column and one of the rows
            alignment.setStart(seq1, seq1Index);
            alignment.setStart(seq2, seq2Index);

            if (direction == 'S' && curMatrix == 0) {
                // we are using matching two characters and staying in the current matrix
                alignment.addCharacter(seq1, seq1.baseAt(seq1Index));
                alignment.addCharacter(seq2, seq2.baseAt(seq2Index));

                curColumn--;
                curRow--;
            } else if (direction == '1' && curMatrix == 0) {
                // we are using two matching characters and jumping to the gap1 sequence matrix
                alignment.addCharacter(seq1, seq1.baseAt(seq1Index));
                alignment.addCharacter(seq2, seq2.baseAt(seq2Index));


                curMatrix = 1;

                curRow--;
                curColumn--;
            } else if (direction == '2' && curMatrix == 0) {

                // we are using two matching characters and jumping to the gap1 sequence matrix
                alignment.addCharacter(seq1, seq1.baseAt(seq1Index));
                alignment.addCharacter(seq2, seq2.baseAt(seq2Index));

                curMatrix = 2;

                curRow--;
                curColumn--;

            } else if (direction == 'C' && curMatrix == 1) {
                // continue an existing gap aligned with seq 1
                // so we have a gap in seq 2
                alignment.addGap(seq2);

                curRow--;
            } else if (direction == 'N' && curMatrix == 1) {
                // we have a new gap based off a non-previous gap. Add gap and jump matrices

                alignment.addGap(seq2);

                // back to original matrix
                curMatrix = 0;

                curRow--;
            } else if (direction == 'C' && curMatrix == 2) {
                // continue an existing gap aligned with seq 2
                // so we have a gap in seq 1
                alignment.addGap(seq1);

                curColumn--;
            } else if (direction == 'N' && curMatrix == 2) {
                // we have a new gap in seq1 (aligned with seq2) based off a non-previous gap. Add gap and jump matrices

                alignment.addGap(seq1);

                // back to original matrix
                curMatrix = 0;

                curColumn--;

            }

        }

        return alignment;
    }
}
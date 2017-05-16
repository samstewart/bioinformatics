package Alignment;

import Model.Alignment;
import Model.LinearScorer;
import Model.Scorer;
import Model.Sequence;

import java.util.Arrays;

import static GUI.Util.print2DCharArray;
import static GUI.Util.print2DIntArray;

/**
 * Does local alignment of two sequences.

 */
public class LocalAligner implements DNAAligner {

    protected Scorer scorer;

    protected boolean biasHighroad;

    public LocalAligner(boolean biasHighroad) {
        scorer = new LinearScorer(-2, -1, 1);

        this.biasHighroad = biasHighroad;

    }


    @Override
    public void initScoresAndTracebacks(int rows, int columns, char[][] tracebacks, int[][] scores) {
        // we need to initialize the score table and put zeros in the appropriate rows and columns
        for (int i = 0; i < rows; i++) {

            // make certain the first column has all zeros
            scores[i][0] = 0;

            if (i == 0) {
                // across first row
                for (int j = 0; j < columns; j++) {
                    scores[i][j] = 0;
                }
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

        // perhaps change to on the stack instead of on the heap?
        int[][] scores = new int[rows][columns];

        // we construct a table for finding the traceback. Each entry is a character (only 8 bits)
        // that tells us where to go at each step.
        // '<' - go to the left, we have a gap in the gene on the vertical axis
        // '^' - go up, we have a gap in the gene on the horizontal axis
        // '`' - go diagonal, we have on gap and consume both characters
        // '0' - no information
        // we don't need the extra row and column
        // but we leave it in for simplicity anyway
        char[][] tracebacks = new char[rows][columns];

        // TODO: are arrays initialized to zero in java by default? what happens if we don't specify the size of the dimension?
        initScoresAndTracebacks(rows, columns, tracebacks, scores);

        // an array for keeping track of the scores of each direction
        int[] dir_scores = new int[4];

        // now we fill in the DP table
        // start at one row below the top and one column to the right
        for (int row = 1; row < rows; row++) {

            // we have to account for the extra row
            int seq1Index = row - 1;

            for (int column = 1; column < columns; column++) {

                // null entry (going nowhere)
                tracebacks[row][column] = '0';

                // we have to account for the extra column
                int seq2Index = column - 1;

                // find the best choice and enter it in the traceback

                // we add in the appropriate biases to break ties

                // we stick these scores in an array and loop through them. The array gives
                // the appropriate ordering for breaking ties using the low road/high road scoring

                // the current ordering is low road scoring

                // gap in seq 1
                dir_scores[0] = scores[row][column - 1] + scorer.score(LinearScorer.GAP, seq2.baseAt(seq2Index));


                // use both character
                dir_scores[1] = scores[row - 1][column - 1] + scorer.score(seq1.baseAt(seq1Index), seq2.baseAt(seq2Index));

                // gap in seq2
                dir_scores[2] = tracebacks[row - 1][column] + scorer.score(seq1.baseAt(seq1Index), LinearScorer.GAP);


                // the zero predecessor or "fresh start" option
                dir_scores[3] = 0;


                int maxScore = Integer.MIN_VALUE;
                int maxIndex = 0;

                // decide which order to go through
                for (int scoreIndex = 0; scoreIndex < dir_scores.length; scoreIndex++) {

                    // decide which direction we are going using bias highroad or not
                    if (dir_scores[(biasHighroad ? 3 - scoreIndex : scoreIndex)] > maxScore) {
                        maxIndex = scoreIndex;
                        maxScore = dir_scores[scoreIndex];
                    }
                }

                switch (maxIndex) {
                    case 0:
                        tracebacks[row][column] = '<';

                        scores[row][column] = maxScore;

                        break;

                    case 1:
                        tracebacks[row][column] = '`';

                        scores[row][column] = maxScore;

                        break;

                    case 2:
                        tracebacks[row][column] = '^';

                        scores[row][column] = maxScore;

                        break;

                    case 3:
                        tracebacks[row][column] = '0';

                        scores[row][column] = 0;

                        break;
                }


            }
        }


        print2DCharArray(tracebacks);
        System.out.print("\n");

        print2DIntArray(scores);

        // now does memory management work for vector?
        return buildAlignmentFromTraceback(seq1, seq2, tracebacks, scores);

    }

    @Override
    public Scorer getScorer() {
        return scorer;
    }

    @Override
    public Alignment buildAlignmentFromTraceback(Sequence seq1, Sequence seq2, char[][] tracebacks, int[][] scores) {
        // we have to search through the entire table for the maximum score
        int rows 	= seq1.length() + 1;
        int columns = seq2.length() + 1;

        int maxScore = 0;
        int maxRow = 0;
        int maxColumn = 0;

        // look through all the rows and columns for the maximum score
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (scores[row][column] > maxScore) {
                    maxRow = row;
                    maxColumn = column;
                }
            }
        }

        // make a new alignment that we are building
        Alignment alignment = new Alignment(seq1, seq2);

        // start the traceback at the best cell
        int curScore = scores[maxRow][maxColumn];
        int curRow = maxRow;
        int curColumn = maxColumn;

        // we need to account for extra column so we subtract off 1
        int seq1Index = curRow    - 1;
        int seq2Index = curColumn - 1;

        // we know where the alignment will end at least
        // we are working backwards
        alignment.setEnd(seq1, seq1Index);
        alignment.setEnd(seq2, seq2Index);

        // we also know this beast's score
        alignment.setScore(maxScore);

        while (curScore != 0) {
            char direction = tracebacks[curRow][curColumn];

            seq1Index = curRow    - 1;
            seq2Index = curColumn - 1;

            if (direction == '^') {
                // gap in sequence 2 along the horizontal axis
                alignment.addGap(seq2);

                curRow--;
            } else if (direction == '<') {
                // gap in sequence 1 along the vertical axis
                alignment.addGap(seq2);

                curColumn--;
            } else if (direction == '`') {
                // use both characters and no gaps
                // so don't do anything
                curRow--;
                curColumn--;

            }

            // update the current score as we work backwards
            // we update *after* following the traceback to ensure we don't lose any characters
            curScore = scores[curRow][curColumn];
        }

        // when we hit a zero, we simply mark the beginning of the two sequences since we were working
        // backward
        alignment.setStart(seq1, seq1Index);
        alignment.setStart(seq2, seq2Index);

        return alignment;
    }
}

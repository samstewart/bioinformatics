package Alignment;

import Model.Alignment;
import Model.LinearScorer;
import Model.Scorer;
import Model.Sequence;

import static GUI.Util.print2DCharArray;
import static GUI.Util.print2DIntArray;

/**
 * Does semi-half global alignment as per the assignment.
 */
public class SemiHalfGlobalAligner extends LocalAligner {

    public SemiHalfGlobalAligner(boolean biasHighroad) {
        super(biasHighroad);
    }



    @Override
    public void initScoresAndTracebacks(int rows, int columns, char[][] tracebacks, int[][] scores) {
        // we initialize the first column full of zeros since
        // we want the chromosome to vary in length.
        // we give the first row a series of gap penalties
        for (int i = 0; i < rows; i++) {

            // make certain the first column has all zeros
            // to allow gaps on the vertical sequence
            scores[i][0] = 0;

            tracebacks[i][0] = '^'; // should have a way to get back up though in reality
                                    // it won't matter because we end at 0

            if (i == 0) {
                // across first row we need penalties for gaps
                for (int j = 1; j < columns; j++) {
                    tracebacks[i][j] = '<'; // we need a way to trace back to zero because you are allowed to have gaps
                                            // in the chromosome on the vertical axis
                    scores[i][j] = j * ((LinearScorer)scorer).gapPenalty;
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
        // 'X' - no information
        // we don't need the extra row and column
        // but we leave it in for simplicity anyway
        char[][] tracebacks = new char[rows][columns];

        // TODO: are arrays initialized to zero in java by default? what happens if we don't specify the size of the dimension?
        initScoresAndTracebacks(rows, columns, tracebacks, scores);

        // an array for keeping track of the scores of each direction
        int[] dir_scores = new int[3];

        // now we fill in the DP table
        // start at one row below the top and one column to the right
        for (int row = 1; row < rows; row++) {

            // we have to account for the extra row
            int seq1Index = row - 1;

            for (int column = 1; column < columns; column++) {

                // null entry (going nowhere)
                tracebacks[row][column] = 'X';

                // we have to account for the extra column
                int seq2Index = column - 1;

                // find the best choice and enter it in the traceback

                // we add in the appropriate biases to break ties

                // we stick these scores in an array and loop through them. The array gives
                // the appropriate ordering for breaking ties using the low road/high road scoring

                // the current ordering is low road scoring; you'd need to reverse it to use highroad

                // gap in seq 1
                dir_scores[0] = scores[row][column - 1] + scorer.score(LinearScorer.GAP, seq2.baseAt(seq2Index));


                // use both character
                dir_scores[1] = scores[row - 1][column - 1] + scorer.score(seq1.baseAt(seq1Index), seq2.baseAt(seq2Index));

                // gap in seq2
                dir_scores[2] = scores[row - 1][column] + scorer.score(seq1.baseAt(seq1Index), LinearScorer.GAP);


                int maxScore = Integer.MIN_VALUE;
                int maxIndex = 0;

                // decide which order to go through
                for (int i = 0; i < dir_scores.length; i++) {

                    // decide which direction we are going using bias highroad or not
                    // if we are going highroad, we need to flip directions.
                    // if we aren't, we keep the same direction.
                    int scoreIndex = (biasHighroad ? (dir_scores.length - 1) - i : i);

                    // must be strictly greater than, otherwise we break ties with order
                    if (dir_scores[scoreIndex] > maxScore) {
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

                }


            }
        }

        System.out.print("\n");

        // now does memory management work for vector?
        return buildAlignmentFromTraceback(seq1, seq2, tracebacks, scores);

    }

    @Override
    public Scorer getScorer() {
        return scorer;
    }

    @Override
    public Alignment buildAlignmentFromTraceback(Sequence seq1, Sequence seq2, char[][] tracebacks, int[][] scores) {
        // we have to search through only the last column for the highest score
        int rows 	= seq1.length() + 1;
        int columns = seq2.length() + 1;
        int lastColumn = columns - 1;

        int maxScore = Integer.MIN_VALUE;
        int maxRow = 0;

        for (int row = 0; row < rows; row++) {

            // look at only the last column
            if (scores[row][lastColumn] > maxScore) {
                maxRow = row;
                maxScore = scores[row][lastColumn];
            }
        }

        // make a new alignment that we are building
        Alignment alignment = new Alignment(seq1, seq2);


        int curRow    = maxRow;
        int curColumn = lastColumn;

        // we need to account for extra column so we subtract off 1
        int seq1Index = curRow    - 1;
        int seq2Index = curColumn - 1;

        // we know where the alignment will end at least
        // we are working backwards
        alignment.setEnd(seq1, seq1Index);
        alignment.setEnd(seq2, seq2Index);

        // we also know this beast's score
        alignment.setScore(maxScore);

        // start at the far right column and work our way left
        while (curColumn != 0) {


            char direction = tracebacks[curRow][curColumn];


            seq1Index = curRow    - 1;
            seq2Index = curColumn - 1;

            // adjust our starting index. this will eventually
            // be the first left column and one of the rows
            alignment.setStart(seq1, seq1Index);
            alignment.setStart(seq2, seq2Index);

            if (direction == '^') {
                // gap in sequence 2 along the horizontal axis
                alignment.addGap(seq2);

                // but use a character in seq1
                alignment.addCharacter(seq1, seq1.baseAt(seq1Index));

                curRow--;
            } else if (direction == '<') {
                // gap in sequence 1 along the vertical axis
                alignment.addGap(seq1);

                // but use a character in seq2
                alignment.addCharacter(seq2, seq2.baseAt(seq2Index));

                curColumn--;
            } else if (direction == '`') {
                alignment.addCharacter(seq1, seq1.baseAt(seq1Index));
                alignment.addCharacter(seq2, seq2.baseAt(seq2Index));
                // use both characters and no gaps
                // so don't do anything
                curRow--;
                curColumn--;

            }
        }


        return alignment;
    }
}

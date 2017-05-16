#include "LocalAligner.h"
#include "LinearScoring.h"
#include <algorithm> // need this for max() function
#include "Alignment.h"

LocalAligner::LocalAligner(bool biasHighroad) {
	
	if (biasHighroad) {
		this->road_bias.upBias = .6;
		this->road_bias.diagonalBias = .4;
		this->road_bias.rightBias = .2;
	} else {
		this->road_bias.upBias = .2;
		this->road_bias.diagonalBias = .4;
		this->road_bias.rightBias = .6;
	}
	
	this->scorer = new LinearScoring(-2, -1, 1);
	
};

LocalAligner::~LocalAligner() {
	delete this->scorer;
};

vector<Alignment*> LocalAligner::findAlignments(Gene *seq1, Gene *seq2) {
	// we construct a new table for the DP algorithm
	
	// the first sequence (seq1) is on the y axis (rows) and the second (seq2) is on the x-axis (columns)
	// we need to add the extra zero columns on either side
	int rows 	= seq1->length() + 1;
	int columns = seq2->length() + 1;
	
	// perhaps change to on the stack instead of on the heap?
	int **scores = new int*[rows];
	
	// we construct a table for finding the traceback. Each entry is a character (only 8 bits)
	// that tells us where to go at each step.
	// '<' - go to the left, we have a gap in the gene on the vertical axis
	// '^' - go up, we have a gap in the gene on the horizontal axis
	// '`' - go diagonal, we have on gap and consume both characters
	// '0' - no information
	// we don't need the extra row and column
	// but we leave it in for simplicity anyway
	char **tracebacks = new char*[rows];
	
	// we need to initialize the score table and put zeros in the appropriate rows and columns
	for (int i = 0; i < rows; i++) {
		scores[i] = new int[columns];
		
		tracebacks[i] = new char[columns];
		
		// make certain the first column has all zeros 
		scores[i][0] = 0;
		
		if (i == 0) {
			// across first row
			for (int j = 0; j < columns; j++) {
				scores[i][j] = 0;
			}
		}
	}
	
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
			
			// gap in seq2
			// we need to subtract an extra -1 to account for the added column
			double up_score = scores[row - 1][column] + scorer->score(seq1->baseAt(seq1Index), LinearScoring::GAP) + road_bias.upBias;
			
			// gap in seq 1
			double left_score = scores[row][column - 1] + scorer->score(LinearScoring::GAP, seq2->baseAt(seq2Index)) + road_bias.rightBias;
			
			// use both character
			double diag_score = scores[row - 1][column - 1] + scorer->score(seq1->baseAt(seq1Index), seq2->baseAt(seq2Index)) + road_bias.diagonalBias;
			
			// now we pick the best option, paying attention to our bias parameter
			// this list of comparisons is pretty annoying, if it were functional
			// how would I do it?
			// Is there a cleaner trick for this?
			// since we have the fractional biases, we always know that we will pick the property option
			if (up_score > left_score && up_score > diag_score) {
				tracebacks[row][column] = '^';
				
				scores[row][column] = up_score;
				
			} else if (left_score > up_score && left_score > diag_score) {
				
				tracebacks[row][column] = '<';
				
				scores[row][column] = left_score;
				
				
			} else if (diag_score > left_score && diag_score > left_score) {
				tracebacks[row][column] = '`';
				
				scores[row][column] = diag_score;
			} 
			
			
		}
	}
	
	// since we have built up all the tracebacks and scoring table, let's find the alignments we have generated
	vector<Alignment*> alignments;
	
	
	// now does memory management work for vector?
	Alignment *alignment = alignmentFromTraceback(tracebacks, scores, seq1, seq2);
	alignments.push_back(alignment);

	// free up the memory we used
	delete tracebacks;
	delete scores;
	
	return alignments;
	
}

Alignment *LocalAligner::alignmentFromTraceback(char **tracebacks, int **scores, Gene *seq1, Gene *seq2) {
	// we have to search through the entire table for the maximum score
	int rows 	= seq1->length() + 1;
	int columns = seq2->length() + 1;
	
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
	Alignment* alignment = new Alignment(seq1, seq2);
	
	// start the traceback at the best cell
	int curScore = scores[maxRow][maxColumn];
	int curRow = maxRow;
	int curColumn = maxColumn;
	
	// we need to account for extra column so we subtract off 1
	int seq1Index = curRow - 1;
	int seq2Index = curColumn - 1;
	
	// we know where the alignment will end at least
	// we are working backwards
	alignment->setEnd(seq1, seq1Index);
	alignment->setEnd(seq2, seq2Index);
	
	// we also know this beast's score
	alignment->setScore((double)maxScore);
	
	while (curScore != 0) {
		char direction = tracebacks[curRow][curColumn];
		
		seq1Index = curRow    - 1;
		seq2Index = curColumn - 1;
		
		if (direction == '^') {
			// gap in sequence 2 along the horizontal axis
			alignment->addGap(seq2, curColumn);
			
			curRow--;
		} else if (direction == '<') {
			// gap in sequence 1 along the vertical axis
			alignment->addGap(seq2, curRow);
			
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
	alignment->setStart(seq1, seq1Index);
	alignment->setStart(seq2, seq2Index);
	
	return alignment;
	
}
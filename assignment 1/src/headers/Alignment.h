#ifndef ALIGNMENT_H
#define ALIGNMENT_H

#include <vector>
#include "Gene.h"

using namespace std;

/** Simple container class to represent an alignment between *two* sequences. */
class Alignment {
	
public:
	// basic constructor
	Alignment(Gene* seq1, Gene* seq2);
	~Alignment();
	
	// actually prints out the alignment to the screen
	void printAlignment();
	
	// add a gap in the alignment right before the given index
	void addGap(Gene *seq, int index);
	
	/** Set the start index of the specified sequence.*/
	void setStart(Gene *sequence, int start);
	
	/** Sets the end index of the specified sequence. */
	void setEnd(Gene *sequence, int end);
	
	/** Sets the score of this particular alignment */
	void setScore(double score);
	
private:
	
	/** the score of this particular alignment*/
	double score;
	
	/** the first DNA sequence */
	Gene* sequence_one;
	
	/** the second DNA sequence */
	Gene* sequence_two;
	
	// the indicies of the sub sequence from the first sequence that matches
	int subsequence_one[2];
	
	// the indices of the sub sequence from the second sequence that matches
	int subsequence_two[2];
	
	// indicies of gaps in sequence one (these indicies indicate a gap right *before* the index)
	vector<int>* gaps_one;
	
	// indicies of gaps in sequence one (these indicies indicate a gap right *before* the index)
	vector<int>* gaps_two;
};

#endif
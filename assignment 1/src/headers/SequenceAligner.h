#ifndef SEQUENCE_ALIGNER_H
#define SEQUENCE_ALIGNER_H

#include <vector>
#include <string>
#include <iostream>
#include "Gene.h"
#include "Alignment.h"

/**
* struct containing biases for the appropriate "road"
*/
typedef struct {
	double rightBias;
	double upBias;
	double diagonalBias;
} RoadBias;

/** 
General base class for aligning to sequences of DNA. This class
is purely virtual. 
*/
class SequenceAligner {
	// constructor that takes road bias?
public:
	/** Find all the possible alignments and return an array. Usually just n of 1 */
	virtual vector<Alignment*> findAlignments(Gene *seq1, Gene *seq2) = 0;
	
protected:
	
	/** the array of bias values controlling how we break ties when examining the scoring matrix */
	RoadBias road_bias;
};

#endif
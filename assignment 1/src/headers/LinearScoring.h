#ifndef LINEAR_SCORING_H
#define LINEAR_SCORING_H

#include "Scoring.h"

/**
* Does simple linear gap scoring
*/
class LinearScoring : public Scoring {
	
public:
	/** we want the various penalties */
	LinearScoring(int gapPenalty, int mismatchPenalty, int matchScore);
	
	/** implement the base class's virtual method*/
	int score(char& a, char& b);
	
private:
	int gapPenalty;
	
	int mismatchPenalty;
	
	int matchScore;
};

#endif
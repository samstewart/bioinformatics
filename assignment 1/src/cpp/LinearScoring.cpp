#include "LinearScoring.h"

LinearScoring::LinearScoring(int gapPenalty, int mismatchPenalty, int matchScore) {
	this->gapPenalty = gapPenalty;
	this->mismatchPenalty = mismatchPenalty;
	this->matchScore = matchScore;
}
/** we finally implement the virtual method*/
int LinearScoring::score(char& a, char& b) {
	if (a == b) {
		return this->matchScore;
	} else if (a != b) {
		return this->mismatchPenalty;
	} else if (a == '-' || b == '-') {
		return this->gapPenalty; // gap penalty of negative 2
	}
	
	return -1;
}

/** the static constant we need to represent a gap*/
char Scoring::GAP = '-';
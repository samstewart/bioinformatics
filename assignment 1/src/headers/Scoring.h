#ifndef SCORING_H
#define SCORING_H

/**
* Base class for scoring alignments. It is a virtual class and thus should not be instantiated.
*/

class Scoring {
public:
	
	/** The character representing a gap */
	static char GAP;
	
	/** scores the two characters. You should pass in a "-" to represent a gap*/
	virtual int score(char& a, char& b) = 0;
};

#endif
#ifndef LOCAL_ALIGNER_H
#define LOCAL_ALIGNER

#include "SequenceAligner.h"
#include "Scoring.h"
#include <vector>
#include "Alignment.h"

/** Implementation of a local alignment algorithm */
class LocalAligner: public SequenceAligner {
	LocalAligner(bool biasHighroad);
	~LocalAligner();
	
private:
	Scoring* scorer;
	
	/** do we favor gaps in the first sequence or in the second sequence? */
	bool biasHighroad;
	
	/** find the best alignment from the traceback */
	Alignment *alignmentFromTraceback(char **tracebacks, int **scores, Gene *seq1, Gene *seq2);
	
	vector<Alignment*> findAlignments(Gene *seq1, Gene *seq2);
};

#endif
#include "Alignment.h"


#include <iostream>
#include <vector>
using namespace std;

Alignment::Alignment(Gene *seq1, Gene *seq2) {
	this->sequence_one = seq1;
	this->sequence_two = seq2;
	
	this->gaps_one = new vector<int>();
	this->gaps_two = new vector<int>();
	
}

Alignment::~Alignment() {
	delete this->gaps_one;
	delete this->gaps_two;
}

void Alignment::printAlignment() {
	// print according to Adam's spec
}

void Alignment::addGap(Gene* seq, int index) {
	if (seq == this->sequence_one) {
		gaps_one->push_back(index);
	} else if (seq == this->sequence_two) {
		gaps_two->push_back(index);
	}
}

void Alignment::setStart(Gene *sequence, int start) {
	if (sequence == this->sequence_one) {
		subsequence_one[0] = start;
	} else if (sequence == this->sequence_two) {
		subsequence_two[0] = start;
	}
}
	
void Alignment::setEnd(Gene *sequence, int end) {
	if (sequence == this->sequence_one) {
		subsequence_one[1] = end;
	} else if (sequence == this->sequence_two) {
		subsequence_two[1] = end;
	}
}

void Alignment::setScore(double score) {
	this->score = score;
}
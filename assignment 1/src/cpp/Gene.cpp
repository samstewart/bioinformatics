#include "Gene.h"

Gene::Gene(string *dna) {
	this->dna = dna;
}

string& Gene::getDNA() {
	string& dna = *(this->dna);
	
	return dna;
}

void Gene::setDNA(string *dna) {
	this->dna = dna;
};

Gene::~Gene() {
	delete this->dna;
};

int Gene::length() {
	return this->dna->length();
};
	
char& Gene::baseAt(int index) {
	return this->dna->at(index);
};

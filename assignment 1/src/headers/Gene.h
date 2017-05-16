#ifndef GENE_H
#define GENE_H

#include <string>

using namespace std;

/** Simple class for holding a sequence of DNA representing a Gene*/
class Gene {
private:
    string* dna;
    
public:
	Gene(string *dna);
	~Gene();
	
    void setDNA(string *dna);

    string& getDNA();

	int length();
	
	char& baseAt(int index);
};

#endif
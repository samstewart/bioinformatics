
#include <string.h>
using std::string;

/** Simple class for holding a sequence of DNA representing a Gene*/

class Gene {
private:
    string *dna;
    
public:
    void setDNA(string *dna);
    string *getDNA();
}
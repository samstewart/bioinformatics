# simple make file grabbed from a website
CC=gcc
IDIR=src/headers
CDIR=src/cpp
ODIR=build
LIBS=-lm -lstdc++
CFLAGS=-I$(IDIR) -Wall

_DEPS = BLAST.h BLASTAligner.h Gene.h GlobalAligner.h LCS.h LocalAligner.h SequenceAligner.h Scoring.h LinearScoring.h Alignment.h GUI.h
DEPS = $(patsubst %,$(IDIR)/%, $(_DEP))

# generating the object files
_OBJ = BLAST.o BLASTAligner.o Gene.o GlobalAligner.o LCS.o LocalAligner.o SequenceAligner.o LinearScoring.o Alignment.o GUI.o
OBJ = $(patsubst %,$(ODIR)/%, $(_OBJ))

# converts c files into object files (we need the headers but makek handles that automatically?)
$(ODIR)/%.o: $(CDIR)/%.cpp $(DEPS)
	$(CC) -c -o $@ $< $(CFLAGS)

newtons_method: $(OBJ)
	$(CC) -o $@ $^ $(CFLAGS) $(LIBS)
    
.PHONY:
	clean

clean:
	rm -f $(ODIR)/*.o localalignment

CC=g++
IDIR=src/headers
ODIR=build
LIBS=-lm
CFLAGS=-I$(IDIR) -Wall


_DEPS = BLAST.h BLASTAligner.h Gene.h GlobalAligner.h LCS.h LocalAligner.h SequenceAligner.h
DEPS = $(patsubst %, $(IDIR)/%, $(_DEP))

_OBJ = BLAST.o BLASTAligner.o Gene.o GlobalAligner.o LCS.o LocalAligner.o SequenceAligner.o
OBJ = $(patsubst %, $(IDIR)/%, $(_OBJ))


$(ODIR)/%.o: %.c $(DEPS)
    $(CC) -c -o $@ $< $(CFLAGS)
    

assignment1: $(OBJ)
    g++ -o $@ $^ $(CFLAGS) $(LIBS)
    
.PHONY: clean

clean:
    rm -f $(ODIR)/*.o assignment1
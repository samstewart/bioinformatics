## Summary
C++ solution to assignment 1 for Professor Smith's Bio-informatics course.

## Layout
I have a number of classes corresponding to the various types of alignment. I have also included some toy problems of the book which I have worked by hand for practice.

## Schedule
This project is due on February 18th which gives me about two weeks to complete it.

I would like to write the base algorithms this week and finish the other algorithms next week.

Wednesday: get everything compiling
Thursday: Make modifications for half-local alignment
## Types of Alignment

### Global
The entirety of both sequences must be used although Indels (deletions and insertions) are allowed

### Local
Any partial subsequence of either sequence can match (we don't have to use all of the sequence).

### Semi-global
The front and back ends of the sequences are allowed gaps with no penalties which is equivalent to saying we can trim off suffixes and prefixes.

### Half-global
Only sequence can lose a suffix or a prefix.


### Assignment
Write a program that will find a certain DNA sequence inside of several other
sequences. You will use this to find antibiotic-resistance genes within
bacteria.

It will input a text file with several lines, each representing a different
sequence. You should align the first line (representing a gene) with each of the
others ("chromosomes"), using a variant of the dynamic programming alignment
algorithm we have been discussing in class.

It should be a half semi-global alignment with a linear gap penalty, such that
the entirety of the gene is aligned against a portion of every chromosome.

When there is a tie while tracing backward, bias it in the following way:

1. Add a gap to the chromosome.
2. Add a gap to neither sequence.
3. Add a gap to the gene.

(This is the "high road" if the chromosome is placed on top of the matrix,
and the "low road" if the gene is placed on top.)

Assume that the scoring matrix gives a +1 if the bases match, and a -1 if they
don't.  The gap penalty will be -2 per base matched with a gap.

For each chromosome, you should output where the best alignment is (i.e. its
start and end with respect to both sequences), its score, and the alignment
itself.

For example, let us say the input file has the following data:

AAGGT
CCCCCCAAGGTCCCCCCC
AAAGCTAT
ACACAGGTAA
CCAAGTCC

Then AAGGT is the gene, and the other lines are chromosomes. The output should
look like this:

best alignment is from [1,7] to [5,11]
score is 5
AAGGT
AAGGT

best alignment is from [1,2] to [5,6]
score is 3
AAGGT
AAGCT

best alignment is from [1,4] to [5,8]
score is 3
AAGGT
CAGGT

best alignment is from [1,3] to [5,6]
score is 2
AAGGT
AAG-T

(The each pair of numbers in brackets represents first the base location in the
gene, and then in the chromosome.)



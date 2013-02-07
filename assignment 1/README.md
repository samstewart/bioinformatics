## Summary
C++ solution to assignment 1 for Professor Smith's Bio-informatics course.

## Layout
I have a number of classes corresponding to the various types of alignment. I have also included some toy problems of the book which I have worked by hand for practice.

## Schedule
This project is due on February 18th which gives me about two weeks to complete it.

I would like to write the base algorithms this week and finish the other algorithms next week.

Wednesday, 6th: Get C++ book and make schedule
Thursday, 7th: Make certain build file works and write class declarations
Friday, 8th: Practice on paper, Implement LCS algorithm
Saturday, 9th: Practice on paper, Implement Global Alignment
Sunday, 10th: Practice on paper, Implement Local Alignment
Monday, 11th: Practice on paper, implement Semi-global Alignment
Tuesday, 12th: Practice on paper, implement half-global alignment
Wednesday, 13th: Add GUI/Unit tests?
## Types of Alignment

### Global
The entirety of both sequences must be used although Indels (deletions and insertions) are allowed

### Local
Any partial subsequence of either sequence can match (we don't have to use all of the sequence).

### Semi-global
The front and back ends of the sequences are allowed gaps with no penalties which is equivalent to saying we can trim off suffixes and prefixes.

### Half-global
Only sequence can lose a suffix or a prefix.


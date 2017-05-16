package Alignment;

import Model.Scorer;
import Model.Sequence;
import Model.Alignment;

/**
 * Very general alignment of two sequences.
 */
public interface Aligner {

    /** aligns two sequences. Seq1 should be on the "vertical axis" or along the side
     * and seq2 should be along the horizontal axis
     * @param seq1 the first sequence we wish to align
     * @param seq2 the second sequence we wish to align
     * @return the best alignment between the two sequences.
     */
    public Alignment findAlignment(Sequence seq1, Sequence seq2);

    /**
     * Get the scorer used for scoring alignments.
     * @return
     */
    public Scorer getScorer();
}

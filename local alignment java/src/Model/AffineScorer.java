package Model;

/**
 * Simple scorer used for affine gap penalties.
 */
public class AffineScorer implements Scorer {


    public int mismatchPenalty;

    public int gapContinuePenalty;

    public int gapStartPenalty;

    public int matchBonus;

    public AffineScorer(int mismatchPenalty, int matchBonus, int gapContinuePenalty, int gapStartPenalty) {
        this.mismatchPenalty        = mismatchPenalty;
        this.gapContinuePenalty     = gapContinuePenalty;
        this.gapStartPenalty        = gapStartPenalty;
        this.matchBonus             = matchBonus;
    }

    @Override
    public int score(char a, char b) {

        if (a == b) {
            return matchBonus;
        } else if (a != b) {
            // note: from the code's perspective a gap will fall under this case which
            // is why we check for gaps first.
            return mismatchPenalty;
        }

        return Integer.MIN_VALUE;
    }



}

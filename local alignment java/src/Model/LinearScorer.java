package Model;

/**
 * Simple scorer that implements the linear scoring model.
 */
public class LinearScorer implements Scorer {
    public int gapPenalty;

    public int mismatchPenalty;

    public int matchBonus;

    public LinearScorer(int gapPenalty, int mismatchPenalty, int matchBonus) {

        this.gapPenalty         = gapPenalty;

        this.mismatchPenalty    = mismatchPenalty;

        this.matchBonus         = matchBonus;
    }


    @Override
    public int score(char a, char b) {
        // we need to check if either is a gap immediately
        if (a == GAP || b == GAP) {
            return gapPenalty;
        }

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

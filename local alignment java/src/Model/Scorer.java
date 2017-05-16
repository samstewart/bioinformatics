package Model;

/**
 * Simple component for scoring two characters from a sequence
 */
public interface Scorer {
    public static final char GAP = '-';

    int score(char a, char b);

}

package Model;

/**
 * Represents a simple sequence of DNA; most often a Gene.
 */
public class Sequence {

    private String dna;

    public Sequence(String dna) {
        this.dna = dna;
    }

    public char baseAt(int index) {
        return this.dna.charAt(index);
    }

    public int length() {
        return dna.length();
    }

    public String toString() {
        return dna.toUpperCase();
    }

    /** specifies a range (inclusive) of the sequence to use */
    public String subsequence(int start, int end) {
        // we have to bump up by +1 because Java returns end - 1
        return dna.substring(start, end  + 1);
    }
}

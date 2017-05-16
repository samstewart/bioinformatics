package gui.parsers;

import tree.Taxon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Parses out the Taxa name and distances between them given a file. Auto-generates alphabetical labels for each item.
 */
public class DistanceParser {

    /** the input file we are reading from */
    private File input_file = null;

    /** the total number of taxa we just parsed*/
    private int numTaxa;

    /** list of taxa we just parsed out of the text file*/
    private List<Taxon> taxa;

    /** the distances we have parsed out of the input file*/
    private int[][] distances;

    public DistanceParser(File input) {
        this.input_file = input;
    }

    public int[][] getDistances() {
        return distances;
    }

    public int getNumTaxa() {
        return numTaxa;
    }

    public List<Taxon> getTaxa() {
        return taxa;
    }

    /**
     * Parses all of the relevant information and places it into our member variables.
     * @param lowerTriangular depends on whether the matrix is lower triangular or not.
     */
    public void parse(boolean lowerTriangular) {
         if (input_file == null) return;

         parseTaxa();

         this.numTaxa   = this.taxa.size();

         parseDistances(lowerTriangular);
    }
    /**
     * Parses a distance matrix (fully filled) given an input file
     * @param lowerTriangular true if the distance matrix is lower triangular, false otherwise
     */
    protected void parseDistances(boolean lowerTriangular) {
        this.distances = new int[numTaxa][numTaxa];

        try {
            Scanner scanner = new Scanner(this.input_file);

            // skip the names
            scanner.nextLine();

            int curLine = 0;

            while (scanner.hasNextLine() && curLine < numTaxa) {

                // loop across each row depending if we are upper or lower triangular matrix
                for (int curTaxa = 0; curTaxa < ( lowerTriangular ? curLine + 1 : numTaxa - curLine); curTaxa++) {
                    this.distances[curLine][curTaxa] = scanner.nextInt();
                }

                curLine++;
            }


            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Parse the taxa out of the specified file
     */
    protected void parseTaxa() {
        this.taxa = new ArrayList<Taxon>();

        try {
            Scanner scanner = new Scanner(this.input_file);
            String[] names = scanner.nextLine().split("\t");

            char auto_name = 'A';

            for (String name : names) {
                taxa.add(new Taxon(name, String.valueOf(auto_name)));

                auto_name++; // next letter of the alphabet
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

package GUI;

import Alignment.AffineGapSemiHalfGlobalAligner;
import Alignment.Aligner;
import Alignment.LocalAligner;
import Alignment.SemiHalfGlobalAligner;
import Model.Sequence;

import java.util.Scanner;

/**
 * Simple interaction with the console for reading in the data and printing out the responses.
 */
public class GUI {

    private static Aligner aligner;

    public static void main(String[] args) {
        // we want to bias the lowroad (put gaps in the chromosome)
        aligner = new AffineGapSemiHalfGlobalAligner(false);

        Scanner scanner = new Scanner(System.in);

        String gene = scanner.nextLine();
        Sequence gene_seq   = new Sequence(gene);

        while (scanner.hasNextLine()) {
            String chromosome = scanner.nextLine();

            Sequence chromo_seq = new Sequence(chromosome);

            // we want the chromosome along the vertical axis and the gene on the top
            // we pass in 'false' to tell it to print out the gene first.
            System.out.println(aligner.findAlignment(chromo_seq, gene_seq).toString(false));
        }

    }

}

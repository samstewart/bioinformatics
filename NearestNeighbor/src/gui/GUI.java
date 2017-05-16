package gui;

import gui.outputters.AdamFormat;
import gui.outputters.NewickFormat;
import gui.outputters.Outputter;
import gui.parsers.DistanceParser;
import tree.Taxon;
import tree.Tree;
import treebuilder.NeighborJoiningTreeBuilder;
import treebuilder.TreeBuilder;

import java.io.File;
import java.util.List;

/**
 * Main entry point for the program to construct a tree.
 * Note: I want a way to output the tree in XML so that I can display it. What about in SGF so that I can
 * publish it?
 */
public class GUI {

    public static void main(String[] argv) {
        if (argv.length == 0) {
            System.out.println("Usage: java gui.GUI [distance file name] [output format]");
            return;
        }



        File input = new File(argv[0]);
        String outputFormat = (argv.length == 2 ? argv[1] : "");

        Outputter outputter;

        if (outputFormat.equals("newick")) {
            outputter = new NewickFormat();
        } else {
            outputter = new AdamFormat();
        }

        System.out.println("Using " + outputter.getClass().getSimpleName() + " for output formatting");
        System.out.println("Loading distance file: " + input.getAbsolutePath());


        DistanceParser parser = new DistanceParser(input);

        System.out.println("Parsing input...");
        parser.parse(true);

        TreeBuilder builder = new NeighborJoiningTreeBuilder();

        List<Taxon> taxa = parser.getTaxa();
        // last item is the outgroup
        Taxon outgroup = taxa.get(taxa.size() - 1);

        System.out.println("Constructing the tree...");
        // we use the last item as the outgroup
        Tree tree = builder.buildTree(parser.getDistances(), taxa, outgroup);

        tree.setOutputter(outputter);

        System.out.println(tree.toString());

    }
}

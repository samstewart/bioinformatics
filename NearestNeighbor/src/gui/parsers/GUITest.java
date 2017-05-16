package gui.parsers;

import gui.GUI;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;

public class GUITest {
    private ByteArrayOutputStream stdout;

    @Before
    public void setup() throws Exception {
        stdout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stdout));
    }
    @Test
    public void testShouldOutputInNewickFormat() {
        GUI.main(new String[] {"src/gui/parsers/data.txt", "newick"});
         assertEquals("Using NewickFormat for output formatting\n" +
                 "Loading distance file: /Users/samstewart/Dropbox/NearestNeighbor/src/gui/parsers/data.txt\n" +
                 "Parsing input...\n" +
                 "Constructing the tree...\n" +
                 "(Ug37 [M12294]:91.0576923076923,(((((NY99-human [AF202541]:3.9419642857142856," +
                 "((((NY99-flamingo [AF196835]:0.0,NJ00-mosquito [AF404754]:0.9999999999999998):0." +
                 "0,(NY00-grouse [AF404755]:0.0,NY00-crow [AF404756]:2.0):0.0):0.0,(CT99-mosquito " +
                 "[AF206518]:2.0,Isr98-stork [AY033389]:9.0):0.0):0.015625,MD00-crow [AF404753]:" +
                 "1.984375):182.1153846153846):182.1153846153846,NY99-horse [AF260967]:2.7734375):" +
                 "182.1153846153846,((Vol99-human [AF317203]:4.488636363636364,Rom96-mosquito [AF260969]:" +
                 "3.511363636363635):1.8062500000000004,It98-horse [AF404757]:3.1937499999999996):1.0034722222222223):" +
                 "182.1153846153846,Eg51 [AF260968]:4.6875000000000036):182.1153846153846,Kun [D00246]:49." +
                 "88461538461539):91.0576923076923);\n", stdout.toString());
    }

    @Test
    public void testShouldOuputInAdamFormat() {
        GUI.main(new String[] {"src/gui/parsers/toy_data.txt"});
        assertEquals("Using AdamFormat for output formatting\n" +
                "Loading distance file: /Users/samstewart/Dropbox/NearestNeighbor/src/gui/parsers/toy_data.txt\n" +
                "Parsing input...\n" +
                "Constructing the tree...\n" +
                "Node Root\t\t\tconnected to ABCDE (2.50), F (2.50)\n" +
                "\tNode ABCDE\t\t\tconnected to ABC (1.00), DE (1.00)\n" +
                "\t\tNode ABC\t\t\tconnected to AB (1.00), C (2.00)\n" +
                "\t\t\tNode AB\t\t\tconnected to A (1.00), B (4.00)\n" +
                "\t\t\t\tNode A\t\t\t(Species 1)\n" +
                "\t\t\t\tNode B\t\t\t(Species 2)\n" +
                "\t\t\tNode C\t\t\t(Species 3)\n" +
                "\t\tNode DE\t\t\tconnected to D (3.00), E (2.00)\n" +
                "\t\t\tNode D\t\t\t(Species 4)\n" +
                "\t\t\tNode E\t\t\t(Species 5)\n" +
                "\tNode F\t\t\t(Species 6)\n\n", stdout.toString());
    }


}

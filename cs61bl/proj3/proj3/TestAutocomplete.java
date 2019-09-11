import ucb.junit.textui;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;


/**
 * The suite of all JUnit tests for the Autocomplete class.
 *
 * @author
 */
public class TestAutocomplete {



    @Test
    public void testTopMatches() {

        try {
            BufferedReader br
                = new BufferedReader(new InputStreamReader(new FileInputStream("wiktionary.txt")));
            int numLines = Integer.valueOf(br.readLine());
            String[] terms = new String[numLines];
            double[] weight = new double[numLines];
            for (int i = 0; i < numLines; i++) {
                String[] line = br.readLine().split("\t");
                double w = Double.valueOf(line[0]);
                String t = line[1];
                terms[i] = t;
                weight[i] = w;
            }
            long start = System.nanoTime();
            Autocomplete a = new Autocomplete(terms, weight);
            System.out.println((System.nanoTime() - start) / 1000 / 1000);
            start = System.nanoTime();
            Iterable ret = null;
            String s = null;
            Random random = new Random();
            for (int i = 0; i < 1; i++) {
                int c1 = random.nextInt(26);
                int c2 = random.nextInt(26);
                s = "";
                ret = a.topMatches("commox", 10);
            }
            System.out.println(s + ":" + ret);
            long end =  System.nanoTime() - start;
            System.out.println(end / 1000 / 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testTopMatch() {
        try {
            BufferedReader br
                    = new BufferedReader
                    (new InputStreamReader(new FileInputStream("wiktionary.txt")));
            int numLines = Integer.valueOf(br.readLine());
            String[] terms = new String[numLines];
            double[] weight = new double[numLines];
            for (int i = 0; i < numLines; i++) {
                String[] line = br.readLine().split("\t");
                double w = Double.valueOf(line[0]);
                String t = line[1];
                terms[i] = t;
                weight[i] = w;
            }
            Autocomplete a = new Autocomplete(terms, weight);
            assertTrue(a.topMatch("th").equals("the"));
            assertTrue(a.topMatch("o").equals("of"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWeightOf() {
        try {
            BufferedReader br
                = new BufferedReader(new InputStreamReader(new FileInputStream("wiktionary.txt")));
            int numLines = Integer.valueOf(br.readLine());
            String[] terms = new String[numLines];
            double[] weight = new double[numLines];
            for (int i = 0; i < numLines; i++) {
                String[] line = br.readLine().split("\t");
                double w = Double.valueOf(line[0]);
                String t = line[1];
                terms[i] = t;
                weight[i] = w;
            }
            long start = System.nanoTime();
            Autocomplete a = new Autocomplete(terms, weight);
            assertTrue(a.weightOf("transition") == 6337.77);
            assertTrue(a.weightOf("of") == 33950064.00);
            assertTrue(a.weightOf("the") == 56271872.00);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the JUnit tests above.
     */
    public static void main(String[] ignored) {
        textui.runClasses(TestAutocomplete.class);
    }

}

import java.util.HashMap;
import java.util.HashSet;

/**
 * Implements autocomplete on prefixes for a given dictionary of terms and weights.
 *
 * @author
 */
public class Autocomplete {
    /**
     * Initializes required data structures from parallel arrays.
     *
     * @param terms Array of terms.
     * @param weights Array of weights.
     */

    private Trie trie;
    private HashMap<String, Double> weightMap;

    public Autocomplete(String[] terms, double[] weights) {
        if (terms.length != weights.length) {
            throw new IllegalArgumentException("arrays not equivalent sizes");
        }
        trie = new Trie();
        HashSet<String> set = new HashSet<>();
        weightMap = new HashMap<>();
        for (int i = 0; i < terms.length; i++) {
            if (set.contains(terms[i])) {
                throw new IllegalArgumentException("Duplicate term");
            }
            if (weights[i] < 0) {
                throw new IllegalArgumentException("bad weight");
            }
            set.add(terms[i]);
            trie.insert(terms[i], weights[i]);
            weightMap.put(terms[i], weights[i]);
        }
        trie.weightMap = weightMap;
    }

    /**
     * Find the weight of a given term. If it is not in the dictionary, return 0.0
     *
     * @param term
     * @return
     */
    public double weightOf(String term) {
        if (weightMap.containsKey(term)) {
            return weightMap.get(term);
        } else {
            return 0.0;
        }
    }

    /**
     * Return the top match for given prefix, or null if there is no matching term.
     *
     * @param prefix Input prefix to match against.
     * @return Best (highest weight) matching string in the dictionary.
     */
    public String topMatch(String prefix) {
        return trie.getTopMatch(prefix);
    }

    /**
     * Returns the top k matching terms (in descending order of weight) as an iterable.
     * If there are less than k matches, return all the matching terms.
     *
     * @param prefix
     * @param k
     * @return
     */
    public Iterable<String> topMatches(String prefix, int k) {
        if (prefix == null) {
            throw new NullPointerException("bruh");
        }
        if (k < 0) {
            throw new IllegalArgumentException("impossible k");
        }

        return trie.topMatches(prefix, k);
    }

    /**
     * Test client. Reads the data from the file, then repeatedly reads autocomplete
     * queries from standard input and prints out the top k matching terms.
     *
     * @param args takes the name of an input file and an integer k as
     *             command-line arguments
     */
    public static void main(String[] args) {
        // initialize autocomplete data structure
        In in = new In(args[0]);
        int N = in.readInt();
        String[] terms = new String[N];
        double[] weights = new double[N];
        for (int i = 0; i < N; i++) {
            weights[i] = in.readDouble();   // read the next weight
            in.readChar();                  // scan past the tab
            terms[i] = in.readLine();       // read the next term
        }

        Autocomplete autocomplete = new Autocomplete(terms, weights);

        // process queries from standard input
        int k = Integer.parseInt(args[1]);
        while (StdIn.hasNextLine()) {
            String prefix = StdIn.readLine();
            for (String term : autocomplete.topMatches(prefix, k)) {
                StdOut.printf("%14.1f  %s\n", autocomplete.weightOf(term), term);
            }
        }
    }
}

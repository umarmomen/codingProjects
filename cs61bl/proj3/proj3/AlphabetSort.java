import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * AlphabetSort takes input from stdin and prints to stdout.
 * The first line of input is the alphabet permutation.
 * The the remaining lines are the words to be sorted.
 * 
 * The output should be the sorted words, each on its own line, 
 * printed to std out.
 */
public class AlphabetSort {

    /**
     * Reads input from standard input and prints out the input words in
     * alphabetical order.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        /* YOUR CODE HERE. */
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            // the first line should contain the alphabet order
            String firstLine = br.readLine();
            if (firstLine == null) {
                throw new IllegalArgumentException();
            }

            // store the alphabets into the map with corresponding order
            NewLanguageTrie t = new NewLanguageTrie(firstLine);

            String nextWord = br.readLine();
            if (nextWord == null) {
                throw new IllegalArgumentException();
            }
            while (nextWord != null) {
                t.insert(nextWord);
                nextWord = br.readLine();
            }

            // sort the arraylist
            ArrayList<String> sortedWords = t.sortedWords();
            for (String word : sortedWords) {
                System.out.println(word);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
